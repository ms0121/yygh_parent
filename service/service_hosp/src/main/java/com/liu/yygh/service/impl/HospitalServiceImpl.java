package com.liu.yygh.service.impl;

import com.liu.yygh.repository.HospitalRepository;
import com.liu.yygh.service.HospitalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-11 - 15:16
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    // 实现数据在mongodb中的增删改查的操作
    @Resource
    private HospitalRepository hospitalRepository;

}
