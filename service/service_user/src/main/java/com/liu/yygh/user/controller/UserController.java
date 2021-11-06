package com.liu.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.UserInfoQueryVo;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-24 - 12:29
 */

//用户后台管理系统,即在后台查询当前的注册用户信息
@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    // 用户列表(条件查询分页)
    //用户列表（条件查询带分页）
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> pageParam = new Page<>(page,limit);
        IPage<UserInfo> pageModel =
                userInfoService.selectPage(pageParam,userInfoQueryVo);
        return Result.ok(pageModel);
    }


    // 用户锁定
    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public Result lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return Result.ok();
    }

    // 用户详情
    @GetMapping("show/{userId}")
    public Result show(@PathVariable Long userId){
        Map<String, Object> map = userInfoService.show(userId);
        return Result.ok(map);
    }

    // 用户认证审批
    @GetMapping("approval/{userId}/{authStatus}")
    public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus){
        userInfoService.approval(userId, authStatus);
        return Result.ok();
    }





}
