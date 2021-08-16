package com.liu.yygh.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-15 - 17:46
 */

@Api(tags = "医院排班信息")
@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin // 实现跨域 springcloud-gateway已经配置了跨域解决的办法
public class ScheduleController {

    @Resource
    private ScheduleService scheduleService;

    /**
     * 根据医院编号hoscode和科室编号depcode进行查询
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @ApiOperation(value = "医院排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable Long page, @PathVariable Long limit,
                                  @PathVariable String hoscode, @PathVariable String depcode){
        Map<String, Object> map = scheduleService.getScheduleRule(page, limit, hoscode, depcode);
        return Result.ok(map);
    }


    /**
     * 根据医院编号，科室编号和工作日期，查询排班的详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @ApiOperation(value = "查询排班的详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(list);
    }







}
