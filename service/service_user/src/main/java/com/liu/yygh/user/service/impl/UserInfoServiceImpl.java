package com.liu.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.JwtHelper;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.user.mapper.UserInfoMapper;
import com.liu.yygh.user.service.PatientService;
import com.liu.yygh.user.service.UserInfoService;
import com.lms.yygh.enums.AuthStatusEnum;
import com.lms.yygh.model.user.Patient;
import com.lms.yygh.model.user.UserInfo;
import com.lms.yygh.vo.user.LoginVo;
import com.lms.yygh.vo.user.UserAuthVo;
import com.lms.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-18 - 17:08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private PatientService patientService;

    // 用户登录验证
    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        // 1.从loginVo对象中获取输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 2、判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        // 3、判断手机号验证码和输入的验证码是否一致,
        String redisCode = redisTemplate.opsForValue().get(phone);

        if (!code.equals(redisCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //绑定手机号码
        UserInfo userInfo = null;
        if (!StringUtils.isEmpty(loginVo.getOpenid())) {
            // 查询数据库中openid是否存在，存在则设置手机号，否则不设置
            userInfo = this.selectWxInfoOpenid(loginVo.getOpenid());
            if (null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }

        if (userInfo == null) {
            // 4、判断是否是第一次登录：根据手机号查询数据库，如果不存在相同的手机号，就是第一次登录
            QueryWrapper<UserInfo> query = new QueryWrapper();
            query.eq("phone", phone);
            userInfo = baseMapper.selectOne(query);
            if (userInfo == null) {
                UserInfo userInfo1 = new UserInfo();
                userInfo1.setName("");
                userInfo1.setPhone(phone);
                userInfo1.setStatus(1);
                // 调用自身的保存用户信息的方法，给新用户进行注册
                this.save(userInfo1);
            }
        }

        // 校验当前用户是否被禁用
//        if (userInfo.getStatus() == 0){
//            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
//        }
        // 5、不是第一次登录，直接登录
        HashMap<String, Object> result = new HashMap<>();
        // 6、返回登录信息
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
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

    // 查询数据库中是否存在当前登录微信的信息
    @Override
    public UserInfo selectWxInfoOpenid(String openId) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openId);
        return baseMapper.selectOne(queryWrapper);
    }

    // 实现用户认证
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        // 查询数据库中用户的信息
        UserInfo userInfo = baseMapper.selectById(userId);
        // 给当前的用户进行设置相应的值，然后进行更新的操作
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        // 设置当前用户的认证状态
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        // 执行更新的操作
        baseMapper.updateById(userInfo);
    }

    // 用户列表(条件查询分页)
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        // 1.根据传入的userInfoQueryVo对象获取条件值
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断


        // 2.对条件值进行非空的判断
        // 3.构建条件查询对象
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(name)) {
            wrapper.like("name", name);
        }
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status", authStatus);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }

        // 调用mp的分页查询方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        // 获取Page中的所有UserInfo对象,然后设置里面的参数值
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }

    // 用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if (status.intValue() == 0 || status.intValue() == 1) {
            System.out.println("status = " + status);
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    // 查询用户详情信息
    @Override
    public Map<String, Object> show(Long userId) {
        HashMap<String, Object> map = new HashMap<>();
        UserInfo userInfo = baseMapper.selectById(userId);
        this.packageUserInfo(userInfo);
        // 封装用户信息
        map.put("userInfo", userInfo);
        // 根据用户ID查询当前用户下的就诊人信息
        List<Patient> patients = patientService.findAll(userId);
        map.put("patientList",patients);
        return map;
    }

    // 用户认证审批
    @Override
    public void approval(Long userId, Integer authStatus) {
        // 2 表示认证通过，  -1 认证不通过
        if (authStatus.intValue() == 2 || authStatus.intValue() == -1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    // 封装当前的userinfo的信息
    //编号变成对应值封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue() == 0 ? "锁定" : "正常";
        userInfo.getParam().put("statusString", statusString);
        return userInfo;
    }


    /**
     * @return
     */
    @ApiOperation(value = "查找相应的所有的信息")
    @GetMapping("find")
    public Result find() {
        return Result.ok();
    }

}
