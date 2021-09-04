package com.liu.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.order.mapper.RefundInfoMapper;
import com.liu.yygh.order.service.RefundInfoService;
import com.lms.yygh.enums.RefundStatusEnum;
import com.lms.yygh.model.order.PaymentInfo;
import com.lms.yygh.model.order.RefundInfo;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lms
 * @date 2021-09-04 - 18:43
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo>
        implements RefundInfoService {
    /**
     * 保存退款记录
     * @param paymentInfo
     * @return
     */
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        // 构建查询条件，查询退款表中是否存在该退款信息
        QueryWrapper<RefundInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", paymentInfo.getOrderId());
        wrapper.eq("payment_type", paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(wrapper);
        // 判断退款表中是否存在该退款信息
        if (refundInfo == null){
            return refundInfo;
        }
        // 不存在则进行封装相应的信息
        refundInfo = new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());

        // 将信息插入到表中
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
