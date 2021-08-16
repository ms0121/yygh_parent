package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.vo.hosp.DepartmentQueryVo;
import com.lms.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-12 - 22:59
 */
public interface DepartmentService {

    // 上传(添加)科室操作
    void save(Map<String, Object> paramMap);

    // 科室列表查询操作
    Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    // 科室删除操作
    void remove(String hoscode, String depcode);

    // 根据医院编号，查询医院的所有科室列表
    List<DepartmentVo> getDeptList(String hoscode);

    // 根据医院编号和科室编号获取科室的名称
    String getDepName(String hoscode, String depcode);
}
