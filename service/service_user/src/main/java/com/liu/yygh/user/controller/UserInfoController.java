package com.liu.yygh.user.controller;

import com.liu.yygh.user.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-18 - 17:10
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

}
