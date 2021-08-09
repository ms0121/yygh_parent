package com.liu.yygh.cmn.controller;

import com.liu.yygh.cmn.service.DictService;
import com.liu.yygh.common.result.Result;
import com.lms.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-09 - 14:09
 */

@Api(tags = "数据字典接口")
@RestController   // responsebody + controller
@CrossOrigin  // 实现跨域
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Resource
    private DictService dictService;

    // 根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        // 根据id进行查询
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }


}
