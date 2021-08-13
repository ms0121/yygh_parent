package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.repository.ScheduleRepository;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-13 - 12:14
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleRepository scheduleRepository;

    // 上传排班信息
    @Override
    public void save(Map<String, Object> paramMap) {
        // 1. 将参数map集合转为对象Schedule
        // 首先使用fastJson将map数据转为字符串,然后将字符串转为相应的对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        // 2.查询mongodb数据库中是否存在该排班信息
        Schedule tempSchedule =
                scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        // 判断当前的Schedule是修改还是添加
        if (tempSchedule != null){
            tempSchedule.setUpdateTime(new Date());
            tempSchedule.setIsDeleted(0);
            tempSchedule.setStatus(1);
            scheduleRepository.save(tempSchedule);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    // 查询医院排班信息
    @Override
    public Page<Schedule> selectPage(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        // 1. 构建分页查询
        // 将查询对象scheduleQueryVo中的数据赋值到Schedule中
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);

        // 设置排序的时间
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页，构建分页对象
        Pageable pageAble = PageRequest.of(page - 1, limit, sort);

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建匹配对象
                // 改变默认字符串的匹配方式:模糊匹配
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true);
        // 创建实例(泛型表示查询之后要返回的数据类型)
        Example<Schedule> example = Example.of(schedule, matcher);

        // 2.执行查询的操作
        Page<Schedule> all = scheduleRepository.findAll(example, pageAble);
        return all;
    }

    // 删除排班信息
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        // 根据医院编号和科室编号从mongodb中查询科室信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}
