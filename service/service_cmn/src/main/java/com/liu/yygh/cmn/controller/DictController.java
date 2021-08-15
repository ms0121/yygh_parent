package com.liu.yygh.cmn.controller;

import com.liu.yygh.cmn.service.DictService;
import com.liu.yygh.common.result.Result;
import com.lms.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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



    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }


    @ApiOperation(value = "数据字典导入")
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importData(file);
        return Result.ok();
    }

    // 将数据字典中的信息导出到excel表格中
    @ApiOperation(value = "数据字典导出")
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response){
        dictService.exportData(response);
    }

    // 根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        // 根据id进行查询
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }


    // 根据DictCode和value进行查询每个dict记录的name属性
    // 查询医院等级
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value){
        String dictName = dictService.getDictName(dictCode, value);
        return dictName;
    }


    // 根据value进行查询每个dict记录的name属性
    @GetMapping("getName/{value}")
    public String getName(@PathVariable("value") String value){
        String dictName = dictService.getDictName("", value);
        return dictName;
    }





}
