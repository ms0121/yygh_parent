package com.liu.yygh.order.service;

import java.util.Map;

/**
 * @author lms
 * @date 2021-09-02 - 9:32
 */
public interface WeixinService {

    // 生成微信支付二维码
    Map createNative(Long orderId);
}
