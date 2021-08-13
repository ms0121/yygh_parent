package com.liu.yygh.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.service.HospitalService;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-13 - 15:44
 */

@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin  // 解决跨区问题
public class HospitalController {

    @Resource
    private HospitalService hospitalService;

    /**
     * 返回所有的医院信息，条件查询分页(因为数据存储在mongodb中，所以返回的分页数据不是mybatis-plus
     * 中的，而是mongodb中的Springdata的Page数据)
     * @param page 查询页
     * @param limit 每页显示的记录数
     * @param hospitalQueryVo 前端传过来的查询条件信息
     * @return
     */
    @ApiOperation("查询医院列表")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page, @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel = hospitalService.selectHosp(page, limit, hospitalQueryVo);
        return Result.ok(page);
    }


}
