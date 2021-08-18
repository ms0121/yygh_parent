package com.liu.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.JwtHelper;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.user.mapper.UserInfoMapper;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-18 - 17:08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {

    // 用户登录验证
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        // 1.从loginVo对象中获取输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 2、判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        // 3、判断手机号验证码和输入的验证码是否一致

        // 4、判断是否是第一次登录：根据手机号查询数据库，如果不存在相同的手机号，就是第一次登录
        QueryWrapper<UserInfo> query = new QueryWrapper();
        query.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(query);
        if (userInfo == null){
            UserInfo userInfo1 = new UserInfo();
            userInfo1.setName("");
            userInfo1.setPhone(phone);
            userInfo1.setStatus(1);
            // 调用自身的保存用户信息的方法，给新用户进行注册
            this.save(userInfo1);
        }
        // 校验当前用户是否被禁用
        if (userInfo.getStatus() == 0){
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        // 5、不是第一次登录，直接登录
        HashMap<String, Object> result = new HashMap<>();
        // 6、返回登录信息
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        // 7、返回登录用户名
        result.put("name", name);
        // 8、返回token信息
        // 使用jwt生成token值
        String token = JwtHelper.createToken(userInfo.getId(), name);
        result.put("token", token);
        return result;
    }


    /**
     *
     * @return
     */
    @ApiOperation(value = "查找相应的所有的信息")
    @GetMapping("find")
    public Result find(){

        return Result.ok();
    }

}
