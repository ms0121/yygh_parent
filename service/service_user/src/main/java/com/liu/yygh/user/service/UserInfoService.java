package com.liu.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.LoginVo;
import com.lms.yygh.vo.user.UserAuthVo;
import com.lms.yygh.vo.user.UserInfoQueryVo;

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

    // 实现用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);

    //用户列表（条件查询带分页）
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    // 用户锁定
    void lock(Long userId, Integer status);

    // 查询当前用户详情信息
    Map<String, Object> show(Long userId);

    // 用户认证审批
    void approval(Long userId, Integer authStatus);
}
