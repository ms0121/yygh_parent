package com.liu.yygh.msm.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.msm.service.MsmService;
import com.liu.yygh.msm.utils.AuthCodeUtil;
import com.liu.yygh.msm.utils.RandomUtil;
import javafx.scene.control.TableView;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author lms
 * @date 2021-08-19 - 14:22
 */

@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Resource
    private MsmService msmService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone) {
        //从redis获取验证码，如果获取获取到，返回ok
        // key 手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果从redis获取不到，
        // 生成验证码，
        AuthCodeUtil.authCode(phone);
        code = AuthCodeUtil.code;
        System.out.println("code = " + code);
        //调用service方法，通过整合短信服务进行发送
        boolean isSend = msmService.send(phone,code);

        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }

}
