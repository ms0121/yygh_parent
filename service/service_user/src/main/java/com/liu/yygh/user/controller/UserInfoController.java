package com.liu.yygh.user.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.utils.AuthContextHolder;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.LoginVo;
import com.lms.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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


    /**
     * 实现用户认证接口
     * @param userAuthVo 用于封装前端传递过来的认证信息(属于请求中传递过来的json数据)
     * @param request 因为当前登录用户的用户id和名称设置在header的token中，即可以从请求域中获取
     *                用户的id信息
     * @return
     */
    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        //传递两个参数，第一个参数用户id，第二个参数认证数据vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        System.out.println("userInfo = " + userInfo);
        return Result.ok(userInfo);
    }
}
