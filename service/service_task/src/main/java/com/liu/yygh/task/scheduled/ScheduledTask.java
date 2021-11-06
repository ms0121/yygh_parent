package com.liu.yygh.task.scheduled;

import com.liu.common.rabbit.constant.MqConst;
import com.liu.common.rabbit.service.RabbitService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-09-04 - 22:06
 */

@Component
@EnableScheduling  // 开启计时功能
public class ScheduledTask {

    @Resource
    private RabbitService rabbitService;

    /**
     * 每天8点执行 提醒就诊
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void task1() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");
    }
}
