package com.liu.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lms
 * @date 2021-08-19 - 11:17
 */

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    // 表示直接从application.yml配置文件中获取指定路径的值赋给当前的属性regionId
    @Value("${aliyun.sms.regionId}")
    private String regionId;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.secret}")
    private String secret;

    // 将从配置文件中获取的值设置在静态变量当中，外部就可以直接使用类名.静态变量获取值
    public static String REGION_Id;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;

    // 调用之前完成赋值操作
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_Id = this.regionId;
        ACCESS_KEY_ID = this.accessKeyId;
        SECRECT = this.secret;
    }
}
