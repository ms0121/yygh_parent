package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.repository.ScheduleRepository;
import com.liu.yygh.service.DepartmentService;
import com.liu.yygh.service.HospitalService;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.BookingScheduleRuleVo;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-13 - 12:14
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleRepository scheduleRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private HospitalService hospitalService;

    @Resource
    private DepartmentService departmentService;

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

    // 根据医院编号hoscode和科室编号depcode在mongodb中进行查询
    @Override
    public Map<String, Object> getScheduleRule(Long page, Long limit, String hoscode, String depcode) {
        //1. 构建查询条件(使用的是MongoTemplate)
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode);

        // 2.根据工作日期workDate进行分组查询
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),  // 匹配查询的条件
                Aggregation.group("workDate")  // 分组的字段，根据哪个字段进行分组
                .first("workDate").as("workDate") // 起别名
                // 3.统计号源的数量信息
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                // 实现排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //4. 实现分页,起始页为 (page - 1) * limit, 以及设值每页显示的记录数
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );

        // 调用方法。实现查询的操作(传入查询的条件，查询的数据类型，返回的数据类型)
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = aggResults.getMappedResults();

        // 分组查询总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        // 获取总记录的数目
        int total = totalAggResults.getMappedResults().size();

        // 把日期转为星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        // 设置最终的数据作为返回值
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);

        // 获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        HashMap<String, Object> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        result.put("baseMap", baseMap);

        return result;
    }

    // 根据医院编号，科室编号和工作日期，查询排班的详细信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        // 根据参数查询mongodb数据库
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode,
                        new DateTime(workDate).toDate());
        // 把得到的ScheduleList集合进行遍历，向每个Schedule进行设置
        // 医院名称，科室名称，日期对应的星期几
        scheduleList.stream().forEach(item -> {
            // 先将list转为相应的流，然后分别进行遍历
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    // 给每个Schedule进行设置: 医院名称，科室名称，日期对应的星期几
    private void packageSchedule(Schedule schedule) {
        // 设置医院的名称
        String hospname = hospitalService.getHospName(schedule.getHoscode());
        schedule.getParam().put("hospname", hospname);

        // 设置医院的科室名称
        String depname = departmentService.getDepName(schedule.getHoscode(), schedule.getDepcode());
        schedule.getParam().put("depname", depname);

        // 设置对应的星期几
        String dayOfWeek = this.getDayOfWeek(new DateTime(schedule.getWorkDate()));
        schedule.getParam().put("dayOfWeek", dayOfWeek);
    }

    /**
     * 根据日期获取周几数据
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
