package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author lms
 * @date 2021-08-11 - 15:16
 */
public interface HospitalService {

    // 上传医院接口
    void save(Map<String, Object> resultMap);

    // 实现根据医院编号查询的操作
    Hospital getByHoscode(String hoscode);

    // mongodb的条件查询分页函数
    Page<Hospital> selectHosp(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);
}
