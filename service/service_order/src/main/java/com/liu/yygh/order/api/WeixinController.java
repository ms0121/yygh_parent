package com.liu.yygh.order.api;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.order.service.PaymentInfoService;
import com.liu.yygh.order.service.WeixinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lms
 * @date 2021-09-02 - 9:31
 */

@RestController
@RequestMapping("/api/order/weixin")
public class WeixinController {

    @Resource
    private WeixinService weixinService;

    @Resource
    private PaymentInfoService paymentService;


    // 生成微信支付二维码
    @GetMapping("/createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }


    // 查询支付状态
    @GetMapping("queryPayStatus/{orderId}")
    public Result queryPayStatus(@PathVariable Long orderId){
        // 调用微信接口实现支付状态的查询
        Map<String, String> resuleMap = weixinService.queryPayStatus(orderId);
        // 判断当前是否存在订单数据信息
        if (resuleMap == null){
            return Result.ok().message("支付出错");
        }
        // 订单支付成功
        if ("SUCCESS".equals(resuleMap.get("trade_state"))){
            // 更新订单的状态
            // 获取当前的订单编码
            String out_trade_no = resuleMap.get("out_trade_no");
            // 然后更新当前的订单表
            paymentService.paySuccess(out_trade_no, resuleMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok().message("支付中");
    }



}
