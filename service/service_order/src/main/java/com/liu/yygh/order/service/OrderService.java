package com.liu.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.order.OrderInfo;

/**
 * @author lms
 * @date 2021-08-27 - 17:30
 */
public interface OrderService extends IService<OrderInfo> {

    // 创建订单
    Long saveOrder(String scheduleId, Long patientId);
}
