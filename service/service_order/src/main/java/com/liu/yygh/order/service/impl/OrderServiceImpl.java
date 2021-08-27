package com.liu.yygh.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.order.mapper.OrderMapper;
import com.liu.yygh.order.service.OrderService;
import com.lms.yygh.model.order.OrderInfo;
import org.springframework.stereotype.Service;

/**
 * @author lms
 * @date 2021-08-27 - 17:31
 */

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo>
        implements OrderService {

    // 创建订单
    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        return null;
    }
}
