package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.repository.ScheduleRepository;
import com.liu.yygh.service.DepartmentService;
import com.liu.yygh.service.HospitalService;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.BookingRule;
import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.BookingScheduleRuleVo;
import com.lms.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
        if (tempSchedule != null) {
            tempSchedule.setUpdateTime(new Date());
            tempSchedule.setIsDeleted(0);
            tempSchedule.setStatus(1);
            scheduleRepository.save(tempSchedule);
        } else {
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
        if (schedule != null) {
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

    // 获取可预约排班数据
    @Override
    public Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode) {
        HashMap<String, Object> result = new HashMap<>();
        // 获取预约规则
        // 根据医院编号获取预约的规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        // 如果医院不存在，则抛出数据异常信息
        if (hoscode == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        // 获取医院中的预约规则信息
        BookingRule bookingRule = hospital.getBookingRule();

        // 获取可预约日期的数据(需要进行分页)
        IPage iPage = this.getListDate(page, limit, bookingRule);
        // 获取可预约日期
        List<Date> dateList = iPage.getRecords();

        // 从mongodb中获取可预约日期里面科室的剩余预约数
        // 先构建查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(dateList);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );

        AggregationResults<BookingScheduleRuleVo> aggregateResult =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> scheduleVoList = aggregateResult.getMappedResults();

        //合并数据 将统计数据ScheduleVo根据“安排日期”合并到BookingRuleVo
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(scheduleVoList)) {
            scheduleVoMap = scheduleVoList.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                    BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }

        // 获取可预约的排班规则
        ArrayList<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            // 从map集合根据key日期获取value值
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            // 如果当前没有排班医生
            if (bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                // 设置就诊医生的人数
                bookingScheduleRuleVo.setDocCount(0);
                // 设置科室剩余预约数，-1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }

            // 设置相关日期
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当前预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条记录为即将预约   状态 0：正常 1：即将放号 -1：当天已停止挂号
            if(i == len-1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间， 不能预约
            if(i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if(stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }

        //可预约日期规则数据
        result.put("bookingScheduleList", bookingScheduleRuleVoList);
        result.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
//月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
//放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
//停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        result.put("baseMap", baseMap);
        return result;
    }

    // 获取可预约日期的数据(需要进行分页)
    private IPage getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        // 获取当前的放号时间，年 月 日 小时 分钟,传入的参数是 当前时间，放号时间
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        // 获取预约周期
        Integer cycle = bookingRule.getCycle();
        // 如果当天放号时间已经过去，预约周期从后一天开始计算，周期+1
        if (releaseTime.isBeforeNow()) {
            cycle += 1;
        }
        // 获取可预约的所有日期，最后一天显示即将放号
        ArrayList<Date> dateList = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        // 因为预约周期是不同的，每页显示日期最多7天数据，超过7天进行分页
        ArrayList<Date> pageDateList = new ArrayList<>();
        int start = (page - 1) * limit;
        int end = (page - 1) * limit + limit;
        // 如果可以显示的数据小于7，直接显示，不需要进行分页
        if (end > dateList.size()) {
            end = dateList.size();
        }
        for (int i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        // 如果可以显示的数据大于7，需要进行分页
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
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
     *
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
