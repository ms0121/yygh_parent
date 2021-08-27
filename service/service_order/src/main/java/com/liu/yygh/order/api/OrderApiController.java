package com.liu.yygh.order.api;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-27 - 17:37
 */

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderApiController {

    @Resource
    private OrderService orderService;

    // 创建订单
    @ApiOperation(value = "创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result saveOrder(@ApiParam(name = "scheduleId", value = "排班id", required = true)
                            @PathVariable String scheduleId,
                            @ApiParam(name = "patientId", value = "就诊人id", required = true)
                            @PathVariable Long patientId) {
        Long orderId = orderService.saveOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }


}
