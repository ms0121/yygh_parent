package com.liu.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.order.PaymentInfo;

/**
 * @author lms
 * @date 2021-09-02 - 9:50
 */
public interface PaymentInfoService extends IService<PaymentInfo> {
    // 向支付记录表添加记录信息
    void savePaymentInfo(OrderInfo order, Integer status);
}
