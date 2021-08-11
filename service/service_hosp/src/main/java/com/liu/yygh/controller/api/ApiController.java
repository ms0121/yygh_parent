package com.liu.yygh.controller.api;

import com.liu.yygh.service.HospitalService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-11 - 15:20
 */

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Resource
    private HospitalService hospitalService;

}
