package com.liu.yygh.msm.receiver;

import com.liu.common.rabbit.constant.MqConst;
import com.liu.yygh.msm.service.MsmService;
import com.lms.yygh.vo.msm.MsmVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-28 - 15:58
 *
 * 封装mq的监听器
 */
@Component
public class SmsReceiver {

    @Resource
    private MsmService msmService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel){
        msmService.send(msmVo);
    }
}
