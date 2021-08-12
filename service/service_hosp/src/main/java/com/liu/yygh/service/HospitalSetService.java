package com.liu.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.hosp.HospitalSet;

/**
 * @author lms
 * @date 2021-07-18 - 21:32
 */
public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);
}
