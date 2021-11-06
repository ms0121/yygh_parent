package com.liu.yygh.user.api;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.helper.JwtHelper;
import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.user.service.UserInfoService;
import com.liu.yygh.user.utils.ConstantWxPropertiesUtils;
import com.liu.yygh.user.utils.HttpClientUtils;
import com.lms.yygh.model.user.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-20 - 12:03
 */
//微信操作接口的实现
@Api(tags = "微信接口实现")
@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Resource
    private UserInfoService userInfoService;


    // 1. 生成微信扫描二维码,需要向前端设置相应的参数
    @ApiOperation(value = "微信二维码参数生成")
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect() {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            // 传入的uri需要进行编码
            String redirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
            map.put("redirectUri", redirectUrl);
            map.put("scope", "snsapi_login");
            map.put("state", System.currentTimeMillis() + "");//System.currentTimeMillis()+""
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. 回调的方法，得到扫描人的信息
    // 微信扫描后回调的方法
    @GetMapping("callback")
    public String callback(String code, String state) {
        //获取授权临时票据
        System.out.println("微信授权服务器回调。。。。。。");
        System.out.println("state = " + state);
        System.out.println("code = " + code);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }

        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        System.out.println("使用code换取的access_token结果 = " + result);

        JSONObject resultJson = JSONObject.parseObject(result);
        if (resultJson.getString("errcode") != null) {
            System.out.println("获取access_token失败：" + resultJson.getString("errcode") + resultJson.getString("errmsg"));
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }

        String accessToken = resultJson.getString("access_token");
        String openId = resultJson.getString("openid");
        System.out.println("accessToken = " + accessToken);
        System.out.println("openId = " + openId);

        //根据access_token获取微信用户的基本信息
        //先根据openid进行数据库查询
        UserInfo userInfo = userInfoService.selectWxInfoOpenid(openId);
        // 如果没有查到用户信息,那么调用微信个人信息获取的接口
        if (null == userInfo) {
            //如果查询到个人信息，那么直接进行登录
            //使用access_token换取受保护的资源：微信的个人信息
            // 使用accessToken, openId再次请求微信地址，获取用户的信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openId);
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            System.out.println("使用access_token获取用户信息的结果 = " + resultUserInfo);

            // 将获取到的string数据转为json形式，方便使用key提取数据信息
            JSONObject resultUserInfoJson = JSONObject.parseObject(resultUserInfo);
            if (resultUserInfoJson.getString("errcode") != null) {
                System.out.println("获取用户信息失败：" + resultUserInfoJson.getString("errcode") +
                        resultUserInfoJson.getString("errmsg"));
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }

            //解析用户信息
            String nickname = resultUserInfoJson.getString("nickname");
            String headimgurl = resultUserInfoJson.getString("headimgurl");

            // 封装用户的对象
            userInfo = new UserInfo();
            userInfo.setOpenid(openId);
            userInfo.setNickName(nickname);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }

        // 获取登录用户的信息，并设置返回值信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        if(StringUtils.isEmpty(userInfo.getPhone())) {
            map.put("openid", userInfo.getOpenid());
        } else {
            map.put("openid", "");
        }
        // 使用jwt生成token数值
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        try {
            // 跳转到前端页面
            return "redirect:" + ConstantWxPropertiesUtils.YYGH_BASE_URL + "/weixin/callback?token="+map.get("token")+
                    "&openid="+map.get("openid")+"&name="+URLEncoder.encode((String) map.get("name"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
