package com.liu.yygh.repository;

import com.lms.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lms
 * @date 2021-08-12 - 22:58
 */
@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    // 根据hoscode, depcode在mongodb中查询医院的科室信息，MongoRepository会根据函数名自动进行查询
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
