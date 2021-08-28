package com.liu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.HttpRequestHelper;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.hosp.client.HospitalFeignClient;
import com.liu.yygh.order.mapper.OrderMapper;
import com.liu.yygh.order.service.OrderService;
import com.liu.yygh.user.client.PatientFeignClient;
import com.lms.yygh.enums.OrderStatusEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.user.Patient;
import com.lms.yygh.vo.hosp.ScheduleOrderVo;
import com.lms.yygh.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    // 创建订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        // 根据就诊人id远程查询就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        // 根据scheduleId远程查询排班信息
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);
        // 判断当前时间是否还可以预约
        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow()
                || new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()){
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }

        // 获取签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
        // 将其添加到订单表中
        OrderInfo orderInfo = new OrderInfo();
        // 将排班表中的信息复制到orderInfo中
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        // 向orderInfo中设置其他数据
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        // 将当前数据信息插入到数据库中
        baseMapper.insert(orderInfo);

        // 调用医院接口，实现预约挂号的操作
        // 首先设置调用医院接口需要的参数，参数放置在map集合中
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",orderInfo.getHoscode());
        paramMap.put("depcode",orderInfo.getDepcode());
        paramMap.put("hosScheduleId",orderInfo.getScheduleId());
        paramMap.put("reserveDate",new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount",orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", sign);

        // 请求医院接口，实现下单操作
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");
        // 根据返回的json数据的状态码code进行判断是否下单成功
        if (result.getInteger("code") == 200){
            JSONObject jsonObject = result.getJSONObject("data");
            // 预约记录唯一的标识(医院预约记录主键)
            String hosRecordId = jsonObject.getString("hosRecordId");
            // 预约的顺序
            Integer number = jsonObject.getInteger("number");
            // 取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            // 取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            // 更新订单信息
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);
            // 排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq信息更新号源和短信通知
        } else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        // 返回订单编号Id
        return orderInfo.getId();
    }
}














