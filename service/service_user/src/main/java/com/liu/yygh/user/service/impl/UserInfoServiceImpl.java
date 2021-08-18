package com.liu.yygh.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.user.mapper.UserInfoMapper;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.model.user.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-18 - 17:08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {

    @Resource
    private UserInfoService userInfoService;


}
