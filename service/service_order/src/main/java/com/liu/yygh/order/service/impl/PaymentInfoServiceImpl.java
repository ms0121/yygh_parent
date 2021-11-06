package com.liu.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.common.helper.HttpRequestHelper;
import com.liu.yygh.hosp.client.HospitalFeignClient;
import com.liu.yygh.order.mapper.PaymentInfoMapper;
import com.liu.yygh.order.service.OrderService;
import com.liu.yygh.order.service.PaymentInfoService;
import com.lms.yygh.enums.PaymentStatusEnum;
import com.lms.yygh.enums.PaymentTypeEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.order.PaymentInfo;
import com.lms.yygh.vo.order.SignInfoVo;
import org.apache.http.client.methods.HttpOptions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lms
 * @date 2021-09-02 - 9:51
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
        implements PaymentInfoService {

    @Resource
    private OrderService orderService;

    @Resource
    private HospitalFeignClient hospitalFeignClient;


    // 向支付记录表添加记录信息
    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentStatus) {
        // 根据订单id和支付状态查询支付表中是否有记录信息
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", order.getId());
        wrapper.eq("payment_type", paymentStatus);
        Integer count = baseMapper.selectCount(wrapper);
        // 说明当前的订单支付记录已存在，直接进行返回
        if (count > 0) {
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentStatus);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd") + "|" + order.getHosname()
                + "|" + order.getDepname() + "|" + order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());
        // 向数据库中添加记录信息
        baseMapper.insert(paymentInfo);
    }


    // 更新订单状态
    @Override
    public void paySuccess(String out_trade_no, Map<String, String> resuleMap) {

        // 1.根据订单编号得到支付记录信息
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        // 订单编号
        wrapper.eq("out_trade_no", out_trade_no);
        // 支付的类型
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);

        // 2.更新支付记录信息
        // 设置支付状态
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(resuleMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resuleMap.toString());
        baseMapper.updateById(paymentInfo);

        // 3.根据订单号得到订单信息
        // 4.更新订单信息
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(PaymentStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);

        // 5.调用医院接口，更新订单支付信息
        // 根据医院编号获取医院的签名信息
        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        // 根据输入的map和医院的签名信息，请求数据签名
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        // 更新支付状态
        HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
    }

    /**
     * 查询支付状态
     * @param orderId 订单id
     * @param paymentType 支付状态
     * @return
     */
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        // 根据订单id和支付的状态进行查询订单的信息
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        return baseMapper.selectOne(wrapper);
    }
}
