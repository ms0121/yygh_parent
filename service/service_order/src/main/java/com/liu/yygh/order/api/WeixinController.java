package com.liu.yygh.order.api;

import com.liu.yygh.common.result.Result;
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

    // 生成微信支付二维码
    @GetMapping("/createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map map = weixinService.createNative(orderId);
        return Result.ok(map);
    }
}
