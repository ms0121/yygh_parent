package com.liu.yygh.user.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lms
 * @date 2021-08-19 - 11:17
 */
@Component
public class ConstantWxPropertiesUtils implements InitializingBean {

    // 表示直接从application.yml配置文件中获取指定路径的值赋给当前的属性regionId
    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Value("${wx.open.redirect_url}")
    private String redirectUrl;

    @Value("${yygh.baseUrl}")
    private String yyghBaseUrl;

    // 将从配置文件中获取的值设置在静态变量当中，外部就可以直接使用类名.静态变量获取值
    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;

    public static String YYGH_BASE_URL;

    // 调用之前完成赋值操作
    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
        YYGH_BASE_URL = yyghBaseUrl;
    }

//    public static void main(String[] args) {
//        ConstantWxPropertiesUtils wx = new ConstantWxPropertiesUtils();
//        System.out.println("WX_OPEN_APP_ID = " + wx.appId);
//    }
}
