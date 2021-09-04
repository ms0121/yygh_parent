package com.liu.yygh.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.vo.order.OrderQueryVo;

/**
 * @author lms
 * @date 2021-08-27 - 17:30
 */
public interface OrderService extends IService<OrderInfo> {

    // 创建订单
    Long saveOrder(String scheduleId, Long patientId);

    // 根据订单id查询订单详情信息
    OrderInfo getOrder(Long orderId);

    // 订单查询（条件查询带分页）
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    // 取消预约的信息
    boolean cancelOrder(Long orderId);


}
