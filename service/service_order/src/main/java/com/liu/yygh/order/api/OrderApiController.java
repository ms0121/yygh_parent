package com.liu.yygh.order.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.utils.AuthContextHolder;
import com.liu.yygh.order.service.OrderService;
import com.lms.yygh.enums.OrderStatusEnum;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    // 根据订单id查询订单详情信息
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrder(@PathVariable Long orderId) {
        System.out.println("orderId = " + orderId);
        OrderInfo orderInfo = orderService.getOrder(orderId);
        return Result.ok(orderInfo);
    }

    // 订单查询（条件查询带分页）
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Integer page,
                       @PathVariable Integer limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request){
        // 查询当前登录用户对应的订单信息
        Long userId = AuthContextHolder.getUserId(request);
        orderQueryVo.setUserId(userId);
        // 构建分页对象
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }

    // 获取订单的状态
    // 因为订单状态我们是封装到枚举中的，页面搜索需要一个下拉列表展示，所以我们通过接口返回页面
    @ApiOperation(value = "获取订单状态")
    @GetMapping("auth/getStatusList")
    public Result getStatusList() {
        return Result.ok(OrderStatusEnum.getStatusList());
    }


    /**
     * 完成取消预约挂号
     * 步骤：
     *  1.根据订单id得到订单信息
     *  2.判断时间
     *  3.调用医院接口实现取消预约信息
     *  4.根据医院接口返回数据进行下面的操作
     *      1）更新订单状态
     *      2）调用危险退款方法
      * @param orderId
     * @return
     */
    @GetMapping("auth/cancleOrder/{orderId}")
    public Result cancleOrder(@PathVariable Long orderId){
        boolean flag = orderService.cancelOrder(orderId);
        return Result.ok(flag);
    }
}
