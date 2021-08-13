package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author lms
 * @date 2021-08-13 - 12:13
 */
public interface ScheduleService {

    // 上传排班的操作
    void save(Map<String, Object> paramMap);

    // 查询排班信息
    Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    // 删除排班信息
    void remove(String hoscode, String hosScheduleId);
}
