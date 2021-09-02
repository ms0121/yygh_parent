package com.liu.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.order.mapper.PaymentInfoMapper;
import com.liu.yygh.order.service.PaymentInfoService;
import com.lms.yygh.enums.PaymentStatusEnum;
import com.lms.yygh.enums.PaymentTypeEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.model.order.PaymentInfo;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author lms
 * @date 2021-09-02 - 9:51
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
        implements PaymentInfoService {

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
}
