package com.liu.yygh.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author lms
 * @date 2021-08-09 - 13:57
 */

@SpringBootApplication
//目的是为了扫描swagger2 的配置类，必须在主启动类添加开启swagger的设置，否则会出现错误
@ComponentScan("com.liu")
@MapperScan("com.liu.yygh.cmn.mapper")
@EnableDiscoveryClient
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }
}
