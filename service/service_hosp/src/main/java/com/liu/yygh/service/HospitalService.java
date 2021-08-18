package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
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
    Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    // 更新医院的上线状态
    void updateStatus(String id, Integer status);

    // 医院详情信息
    Map<String, Object> showHospDetail(String id);

    // 获取医院的名称
    String getHospName(String hoscode);

    // 根据医院名称进行模糊查询医院的信息
    List<Hospital> findByHosName(String hosname);

    // 根据医院编号获取医院预约挂号详情
    Map<String, Object> item(String hoscode);
}
