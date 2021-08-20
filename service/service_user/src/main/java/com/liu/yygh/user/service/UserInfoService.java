package com.liu.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * @author lms
 * @date 2021-08-18 - 17:07
 */
public interface UserInfoService extends IService<UserInfo> {

    // 用户登录验证
    Map<String, Object> login(LoginVo loginVo);

    // 查询数据库中是否存在当前登录微信的信息
    UserInfo selectWxInfoOpenid(String openId);
}
