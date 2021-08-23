package com.liu.yygh.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author lms
 * @date 2021-08-19 - 11:17
 * 该工具类用于读取配置文件中的信息
 */

@Component
public class ConstantOssPropertiesUtils implements InitializingBean {

    // 表示直接从application.yml配置文件中获取指定路径的值赋给当前的属性
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.secret}")
    private String secret;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    // 将从配置文件中获取的值设置在静态变量当中，外部就可以直接使用类名.静态变量获取值
    public static String ENDPOINT;
    public static String ACCESS_KEY_ID;
    public static String SECRECT;
    public static String BUCKET;

    // 调用之前完成赋值操作
    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = this.endpoint;
        ACCESS_KEY_ID = this.accessKeyId;
        SECRECT = this.secret;
        BUCKET = this.bucket;
    }
}
