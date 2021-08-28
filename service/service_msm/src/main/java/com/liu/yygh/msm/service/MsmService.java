package com.liu.yygh.msm.service;

import com.lms.yygh.vo.msm.MsmVo;

/**
 * @author lms
 * @date 2021-08-19 - 14:21
 */
public interface MsmService {

    // 发送手机验证码
    boolean send(String phone, String code);

    // mq发送短信封装
    boolean send(MsmVo msmVo);

}
