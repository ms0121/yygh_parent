package com.liu.yygh.controller.api;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.service.DepartmentService;
import com.liu.yygh.service.HospitalService;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.vo.hosp.DepartmentVo;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

}







