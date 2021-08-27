package com.liu.yygh.service;

import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
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

    // 根据医院编号hoscode和科室编号depcode进行查询
    Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode);

    // 根据医院编号，科室编号和工作日期，查询排班的详细信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    // 获取可预约排班数据
    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    // 根据排班id获取排班数据
    Schedule getById(String scheduleId);
}
