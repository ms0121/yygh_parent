package com.liu.common.rabbit.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-28 - 12:38
 */

@Service
public class RabbitService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息(要实现消息的发送，还需要配置消息转换器)
     * @param exchange 交换机
     * @param routingKey 路由key
     * @param message 消息
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
