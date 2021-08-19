package com.liu.yygh.msm.service;

/**
 * @author lms
 * @date 2021-08-19 - 14:21
 */
public interface MsmService {

    // 发送手机验证码
    boolean send(String phone, String code);
}
