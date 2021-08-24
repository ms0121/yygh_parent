package com.liu.yygh.user.api;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.common.utils.AuthContextHolder;
import com.liu.yygh.user.service.PatientService;
import com.lms.yygh.model.user.Patient;
import com.sun.xml.internal.ws.resources.HttpserverMessages;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-23 - 21:43
 */
@RestController
@RequestMapping("/api/user/patient")
public class PatientApiController {

    @Resource
    private PatientService patientService;


    /**
     * 根据当前登录用户的ID信息来获取病人信息
     * @param request
     * @return
     */
    @GetMapping("auth/findAll")
    public Result findAll(HttpServletRequest request){
        // 获取当前登录用户的id号
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> patients = patientService.findAll(userId);
        return Result.ok(patients);
    }

    /**
     * 添加就诊人信息
     * @param patient 就诊人信息
     * @param request 获取当前登录用户的id信息，将其设置在就诊人的user_id中
     * @return
     */
    @PostMapping("auth/save")
    public Result savePatient(@RequestBody Patient patient, HttpServletRequest request){
        // 获取当前登录的用户id
        Long userId = AuthContextHolder.getUserId(request);
        // 将当前用户的id进行设置
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }

    // 根据id获取就诊人信息
    @GetMapping("auth/get/{id}")
    public Result getPatient(@PathVariable Long id){
        Patient patient = patientService.getPatientById(id);
        return Result.ok(patient);
    }

    // 修改就诊人信息
    @PostMapping("auth/update")
    public Result updatePatient(@RequestBody Patient patient){
        // 根据就诊人的id进行更新就诊人的信息
        patientService.updateById(patient);
        return Result.ok();
    }

    // 删除就诊人
    @DeleteMapping("auth/remove/{id}")
    public Result removePatient(@PathVariable Long id){
        // 根据就诊人的id进行删除就诊人的信息
        patientService.removeById(id);
        return Result.ok();
    }
}
