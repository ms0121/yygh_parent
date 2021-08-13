package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

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
}
