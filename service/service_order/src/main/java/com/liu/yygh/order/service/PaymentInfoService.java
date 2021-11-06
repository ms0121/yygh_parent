package com.liu.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.order.PaymentInfo;

import java.util.Map;

/**
 * @author lms
 * @date 2021-09-02 - 9:50
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    // 向支付记录表添加记录信息
    void savePaymentInfo(OrderInfo order, Integer status);

    // 更新订单表
    void paySuccess(String out_trade_no, Map<String, String> resuleMap);

    // 获取支付记录
    // 因为退款是根据付款记录进行操作的
    PaymentInfo getPaymentInfo(Long orderId, Integer paymentType);
}
