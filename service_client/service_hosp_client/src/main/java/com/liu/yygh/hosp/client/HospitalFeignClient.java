package com.liu.yygh.hosp.client;

import com.lms.yygh.vo.hosp.ScheduleOrderVo;
import com.lms.yygh.vo.order.SignInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lms
 * @date 2021-08-27 - 20:15
 */

@FeignClient(value = "service-hosp")
@Repository
public interface HospitalFeignClient {

    //根据排班id获取预约下单数据
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    ScheduleOrderVo getScheduleOrderVo(@PathVariable("scheduleId") String scheduleId);

    // 根据医院编号获取医院的签名信息
    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    SignInfoVo getSignInfoVo(@PathVariable("hoscode") String hoscode);
}
