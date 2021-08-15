package com.liu.yygh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author lms
 * @date 2021-07-18 - 20:38
 */
@SpringBootApplication
//目的是为了扫描swagger2 的配置类，必须在主启动类添加开启swagger的设置，否则会出现错误
@ComponentScan(basePackages = "com.liu")
@MapperScan("com.liu.yygh.mapper")
@EnableSwagger2
@EnableFeignClients(basePackages = "com.liu")
@EnableDiscoveryClient
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}


