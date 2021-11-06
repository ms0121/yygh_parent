package com.liu.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.hosp.HospitalSet;
import com.lms.yygh.vo.order.SignInfoVo;

/**
 * @author lms
 * @date 2021-07-18 - 21:32
 */
public interface HospitalSetService extends IService<HospitalSet> {
    // 根据传递过来的医院编号，查询数据库，查询签名信息
    String getSignKey(String hoscode);

    // 根据医院编号获取医院的签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
