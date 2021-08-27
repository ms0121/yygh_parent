package com.liu.yygh.user.client;

import com.liu.yygh.common.result.Result;
import com.lms.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lms
 * @date 2021-08-27 - 19:27
 * FeignClient:表示当前要调用的微服务，接口中填写的是要被调用的微服务接口，需要填写完成的路径信息
 */
@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    // 根据就诊人id获取就诊人信息
    @GetMapping("/api/user/patient/inner/get/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);
}
