package com.liu.yygh.repository;

import com.lms.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lms
 * @date 2021-08-11 - 15:13
 */

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

    // mongodb会自动的根据函数名进行创建函数，所以不需要再编写函数具体的实现部分
    Hospital getHospitalByHoscode(String hoscode);

    // 根据医院名称进行模糊查询医院的信息
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
