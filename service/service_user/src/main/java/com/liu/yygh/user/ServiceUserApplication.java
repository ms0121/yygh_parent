package com.liu.yygh.user;

import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lms
 * @date 2021-08-18 - 16:52
 */
@Api(tags = "用户操作")
@SpringBootApplication
@ComponentScan(basePackages = "com.liu")
//扫描mapper文件
@MapperScan("com.liu.yygh.user.mapper")
//开启swagger2和服务注册与发现以及远程调用
@EnableSwagger2
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.liu")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class, args);
    }
}
