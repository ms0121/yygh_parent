package com.liu.yygh.service.impl;

import com.liu.yygh.repository.DepartmentRepository;
import com.liu.yygh.service.DepartmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-12 - 23:01
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;

}
