package com.liu.yygh.user.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-18 - 17:10
 */
@Api(tags = "用户操作")
@RestController
@RequestMapping("/api/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * @param loginVo 用于封装登录用户的手机，验证码等信息
     * @return
     * 验证码的实现细节：实质就是程序自动生成的验证码，然后通过程序发送给手机，和将该验证码设置在redis缓存中(并设置过期时间)
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info = userInfoService.login(loginVo);
        return Result.ok(info);
    }

}
