package com.liu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.common.rabbit.constant.MqConst;
import com.liu.common.rabbit.service.RabbitService;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.HttpRequestHelper;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.hosp.client.HospitalFeignClient;
import com.liu.yygh.order.mapper.OrderMapper;
import com.liu.yygh.order.service.OrderService;
import com.liu.yygh.order.service.WeixinService;
import com.liu.yygh.user.client.PatientFeignClient;
import com.lms.yygh.enums.OrderStatusEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.user.Patient;
import com.lms.yygh.vo.hosp.ScheduleOrderVo;
import com.lms.yygh.vo.msm.MsmVo;
import com.lms.yygh.vo.order.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author lms
 * @date 2021-08-27 - 17:31
 */

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo>
        implements OrderService {

    // 使用远程调用接口查询用户的信息和排班信息
    @Resource
    private PatientFeignClient patientFeignClient;

    @Resource
    private HospitalFeignClient hospitalFeignClient;

    @Resource
    private RabbitService rabbitService;

    @Resource
    private WeixinService weixinService;


    // 创建订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        //        // 根据就诊人id远程查询就诊人信息
        //        Patient patient = patientFeignClient.getPatientOrder(patientId);
        //        // 根据scheduleId远程查询排班信息
        //        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        //        // 判断当前时间是否还可以预约
        //        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
        //                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()){
        //            throw new YyghException(ResultCodeEnum.TIME_NO);
        //        }
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //获取排班相关信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //判断当前时间是否还可以预约
        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }

        //获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());

        //添加到订单表
        OrderInfo orderInfo = new OrderInfo();
        //scheduleOrderVo 数据复制到 orderInfo
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //向orderInfo设置其他数据
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);

        //调用医院接口，实现预约挂号操作
        //设置调用医院接口需要参数，参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("depcode", orderInfo.getDepcode());
        paramMap.put("hosScheduleId", orderInfo.getScheduleId());
        paramMap.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount", orderInfo.getAmount());

        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        //请求医院系统接口
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");
        String jsonString = result.toJSONString();
        System.out.println("jsonString = " + jsonString);

        if (result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            ;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            ;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            ;
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq消息，号源更新和短信通知
            //发送mq信息更新号源
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);

            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }


    // 根据订单id查询订单详情信息
    // 这部分代码还有问题
    @Override
    public OrderInfo getOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // System.out.println("orderInfo.getId() = " + orderInfo.getId());
        return this.packOrderInfo(orderInfo);
    }


    // 订单查询（条件查询带分页）
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();

        String hosname = orderQueryVo.getKeyword(); // 医院名称
        Long patientId = orderQueryVo.getPatientId(); // 就诊人id
        String orderStatus = orderQueryVo.getOrderStatus(); // 订单状态
        String reserveDate = orderQueryVo.getReserveDate(); // 安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin(); // 创建时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();

        // 判断查询的条件是否为空
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.eq("hosname", hosname);
        }
        if (!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }

        // 查询分页信息
        Page<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        // 对订单中的信息进行封装
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }

    /**
     * 取消预约的信息
     *
     * @param orderId
     * @return
     */
    @Override
    public boolean cancelOrder(Long orderId) {
        // 根据id获取当前的订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        // 如果当前时间大于退号时间，不能取消预约
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        // 调用医院接口实现预约取消的操作
        // 调用医院接口获取医院信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if (signInfoVo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        // 封装信息
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        // 调用医院接口完成操作
        // signInfoVo.getApiUrl()获取医院的访问地址
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updateCancelStatus");

        if (result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            // 是否支付过退款
            if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                // 已支付退款
                Boolean refund = weixinService.refund(orderId);
                if (!refund) {
                    throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
            }

            // 更改订单状态
            orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());

            //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数减1即可
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(orderInfo.getScheduleId());
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") +
                    (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        }
        return true;

    }

    // 就诊提醒
    @Override
    public void patientTips() {
        // 根据日期查询当前需要被提醒的就诊人
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        for (OrderInfo orderInfo : orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }


    /**
     * 预约统计信息
     * 因为要绘制图表信息，需要的数据是数组的形式，但是要转为接送的数据信息，所以
     * 我们使用的是返回list列表的信息，然后使用map进行数据的封装
      * @param orderCountQueryVo
     * @return
     */
    @Override
    public Map<String, Object> getCountOrder(OrderCountQueryVo orderCountQueryVo) {
        // 获取到每个预约时间的预约人数数量信息列表
        // 先获取到数据信息
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        // 获取x需要的数据信息，list集合（数组），所以可以使用stream流的方式进行获取每个对象的预约日期
        // 先将得到的list转为stream流，然后使用map进行提取指定的属性信息，最后转为list集合
        List<String> dateList =
                orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        // 获取y都需要的数据信息，list集合（数组）的形式，
        List<Integer> countList =
                orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        Map<String, Object> map = new HashMap<>();
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }


    // 对订单信息的封装
    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString",
                OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}


