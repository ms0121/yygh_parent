package com.liu.yygh.repository;

import com.lms.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lms
 * @date 2021-08-13 - 12:13
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    // 查询排班信息
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
