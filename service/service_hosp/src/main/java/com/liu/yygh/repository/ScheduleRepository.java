package com.liu.yygh.repository;

import com.lms.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-13 - 12:13
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    // 查询排班信息
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    // 根据医院编号，科室编号和工作日期，查询排班的详细信息
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);

}
