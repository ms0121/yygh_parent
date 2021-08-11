package com.liu.yygh.repository;

import com.lms.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lms
 * @date 2021-08-11 - 15:13
 */

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String> {

}
