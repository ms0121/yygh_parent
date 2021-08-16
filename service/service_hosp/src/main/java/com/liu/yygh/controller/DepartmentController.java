package com.liu.yygh.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.service.DepartmentService;
import com.lms.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-15 - 12:04
 */
@Api(tags = "科室信息接口")
@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin  // 实现跨域 springcloud-gateway已经配置了跨域解决的办法
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 根据医院编号，查询医院的所有科室列表
     * @param hoscode
     * @return
     */
    @ApiOperation(value = "查询所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.getDeptList(hoscode);
        return Result.ok(list);
    }

}
