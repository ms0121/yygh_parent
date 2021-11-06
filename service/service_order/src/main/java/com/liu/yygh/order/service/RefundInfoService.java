package com.liu.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.order.PaymentInfo;
import com.lms.yygh.model.order.RefundInfo;

/**
 * @author lms
 * @date 2021-09-04 - 18:43
 */
public interface RefundInfoService extends IService<RefundInfo> {
    /**
     * 保存退款记录
     * 将获取到的支付信息传入到退款记录表中进行保存
     * @param paymentInfo
     */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);

}
