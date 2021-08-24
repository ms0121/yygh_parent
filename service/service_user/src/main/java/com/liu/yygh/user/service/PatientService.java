package com.liu.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.user.Patient;

import java.util.List;

/**
 * @author lms
 * @date 2021-08-23 - 21:44
 */
public interface PatientService extends IService<Patient> {
    // 根据当前登录用户的ID信息来获取病人信息
    List<Patient> findAll(Long userId);

    // 根据id查询用户的信息
    Patient getPatientById(Long id);
}
