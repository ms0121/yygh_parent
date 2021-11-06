package com.liu.yygh.controller.api;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.service.DepartmentService;
import com.liu.yygh.service.HospitalService;
import com.liu.yygh.service.HospitalSetService;
import com.liu.yygh.service.ScheduleService;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.model.hosp.Schedule;
import com.lms.yygh.vo.hosp.DepartmentVo;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import com.lms.yygh.vo.hosp.ScheduleOrderVo;
import com.lms.yygh.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-17 - 15:35
 */

@Api(tags = "医院前端接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {

    @Resource
    private HospitalService hospitalService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private HospitalSetService hospitalSetService;


    /**
     * 查询医院的列表信息(分页查询)
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation(value = "查询医院列表")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals =
                hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        return Result.ok(hospitals);
    }


    @ApiOperation(value = "根据医院名称查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
        List<Hospital> list = hospitalService.findByHosName(hosname);
        return Result.ok(list);
    }


    @ApiOperation(value = "根据医院编号获取科室列表")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> deptList = departmentService.getDeptList(hoscode);
        return Result.ok(deptList);
    }


    @ApiOperation(value = "根据医院编号获取医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String, Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }


    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(
            @ApiParam(name = "page", value = "当前页面", required = true)
            @PathVariable Integer page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Integer limit,
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode){

        // 查询可预约的排班数据信息
        return Result.ok(scheduleService.getBookingScheduleRule(page, limit, hoscode, depcode));
    }


    @ApiOperation(value = "获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable String hoscode,
            @ApiParam(name = "depcode", value = "科室code", required = true)
            @PathVariable String depcode,
            @ApiParam(name = "workDate", value = "排班日期", required = true)
            @PathVariable String workDate) {
        return Result.ok(scheduleService.getDetailSchedule(hoscode, depcode, workDate));
    }

    // 根据排班id获取排班数据
    @ApiOperation(value = "根据排班id获取排班数据")
    @GetMapping("getSchedule/{scheduleId}")
    public Result getSchedule(@ApiParam(name = "scheduleId", value = "排班id", required = true)
                                  @PathVariable String scheduleId){
        Schedule schedule = scheduleService.getById(scheduleId);
        return Result.ok(schedule);
    }

    //根据排班id获取预约下单数据
    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleOrderVo(scheduleId);
    }


    // 根据医院编号获取医院的签名信息
    @ApiOperation(value = "根据医院编号获取医院的签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(
            @ApiParam(name = "hoscode", value = "医院code", required = true)
            @PathVariable("hoscode") String hoscode) {
        return hospitalSetService.getSignInfoVo(hoscode);
    }

}

