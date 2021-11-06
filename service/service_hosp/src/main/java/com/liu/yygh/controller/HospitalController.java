 package com.liu.yygh.controller;

 import com.liu.yygh.common.result.Result;
 import com.liu.yygh.service.HospitalService;
 import com.lms.yygh.model.hosp.Hospital;
 import com.lms.yygh.vo.hosp.HospitalQueryVo;
 import io.swagger.annotations.Api;
 import io.swagger.annotations.ApiOperation;
 import org.springframework.data.domain.Page;
 import org.springframework.web.bind.annotation.*;

 import javax.annotation.Resource;
 import java.util.List;
 import java.util.Map;

 /**
 * @author lms
 * @date 2021-08-13 - 15:44
 */

 @Api(tags = "医院管理功能")
@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin  // 解决跨区问题// 实现跨域 springcloud-gateway已经配置了跨域解决的办法
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
        Page<Hospital> pageModel = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        List<Hospital> content = pageModel.getContent();
        long elements = pageModel.getTotalElements();
        System.out.println("content = " + content);
        return Result.ok(pageModel);
    }


     /**
      * 更新医院的上线状态
      * @param id
      * @param status
      * @return
      */
     @ApiOperation(value = "更新上线状态")
     @GetMapping("updateStatus/{id}/{status}")
     public Result updateStatus(@PathVariable String id, @PathVariable Integer status){
        hospitalService.updateStatus(id, status);
        return Result.ok();
     }

     /**
      * 医院详情信息
      * @param id
      * @return
      */
     @ApiOperation(value = "医院详情信息")
     @GetMapping("showHospDetail/{id}")
     public Result showHospDetail(@PathVariable("id") String id){
        Map<String, Object> map = hospitalService.showHospDetail(id);
        return Result.ok(map);
     }



}
