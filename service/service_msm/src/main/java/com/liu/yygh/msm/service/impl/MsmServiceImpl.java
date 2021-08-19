package com.liu.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.liu.yygh.msm.service.MsmService;
import com.liu.yygh.msm.utils.ConstantPropertiesUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-19 - 14:22
 */
@Service
public class MsmServiceImpl implements MsmService {

    // 发送手机验证码
    @Override
    public boolean send(String phone, String code) {
        // 判断手机号
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            return false;
        } else {
            return true;
        }


//        // 整合阿里云的短信服务
//        // 设置阿里云相关参数
//        DefaultProfile profile = DefaultProfile.
//                getProfile(ConstantPropertiesUtils.REGION_Id,
//                        ConstantPropertiesUtils.ACCESS_KEY_ID,
//                        ConstantPropertiesUtils.SECRECT);
//        IAcsClient client = new DefaultAcsClient(profile);
//        CommonRequest request = new CommonRequest();
//        //request.setProtocol(ProtocolType.HTTPS);
//        request.setMethod(MethodType.POST);
//        request.setDomain("dysmsapi.aliyuncs.com");
//        request.setVersion("2017-05-25");
//        request.setAction("SendSms");
//
//        //手机号
//        request.putQueryParameter("PhoneNumbers", phone);
//        //签名名称
//        request.putQueryParameter("SignName", "我的谷粒在线教育网站");
//        //模板code
//        request.putQueryParameter("TemplateCode", "SMS_180051135");
//        //验证码  使用json格式   {"code":"123456"}
//        Map<String,Object> param = new HashMap();
//        param.put("code",code);
//        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));
//
//        //调用方法进行短信发送
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            System.out.println("成功执行了函数");
//            System.out.println(response.getData());
//            return response.getHttpResponse().isSuccess();
//        } catch (ServerException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
    }
}
