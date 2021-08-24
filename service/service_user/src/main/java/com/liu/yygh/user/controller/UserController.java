package com.liu.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.UserInfoQueryVo;
import org.joda.time.ReadableInstant;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
        List<UserInfo> records = pageModel.getRecords();
        System.out.println("records = " + records);
        return Result.ok(pageModel);
    }

}
