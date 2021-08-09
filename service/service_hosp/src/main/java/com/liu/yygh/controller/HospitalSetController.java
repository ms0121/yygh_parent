package com.liu.yygh.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liu.yygh.service.HospitalSetService;
import com.lms.yygh.common.result.Result;
import com.lms.yygh.common.utils.MD5;
import com.lms.yygh.model.hosp.HospitalSet;
import com.lms.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * @author lms
 * @date 2021-07-18 - 21:37
 */

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin   // 允许跨域
public class HospitalSetController {

    @Resource
    private HospitalSetService hospitalSetService;

    // 访问路径：http://localhost:8201/admin/hosp/hospitalSet/findall
    // 查询所有的医院信息
    @ApiOperation(value = "获取分页列表")
    @GetMapping("findAll")
    public Result findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    /**
     * 逻辑删除医院设置的操作
     * @param id
     * @return
     */
    @ApiOperation(value = "删除医院设置")
    @DeleteMapping("{id}")
    public Result deleteHosp(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }


    // 3.条件查询带分页信息
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPage(@PathVariable long current,
                           @PathVariable long limit,
                           // @RequestBody(required = false) RequestBody表示当前的参数按照json的方式进行提交，
                           // required = false 表示当前的参数可传或者不传，
                           // 请求方式必须为PostMapping
                           @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){
        // 创建page对象，传递当前页，每页的记录数
        Page<HospitalSet> page = new Page<>(current, limit);

        // 构建查询的条件
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();

        // 查询条件可有可无
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        // 条件不为空则进行检查匹配
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname", hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode", hospitalSetQueryVo.getHoscode());
        }

        // 调用mybatis-plus中的方法实现分页调用
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        // 返回数据
        return Result.ok(hospitalSetPage);
    }

    /**
     * 4.添加医院设置
     * @RequestBody 表示当前的这个参数为必须
     * @param hospitalSet
     * @return
     */
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        // 添加医院数据之后要进行参数的校验工作
        // 设置医院的状态值
        hospitalSet.setStatus(0);
        // 设置签名秘钥,使用md5进行加密
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()
                + "" + random.nextInt(1000)));

        // 调用service
        boolean flag = hospitalSetService.save(hospitalSet);
        if (flag) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 5.根据id获取医院设置
    @GetMapping("getHospitalSet/{id}")
    public Result getHospitalSetById(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    // 6.修改医院设置
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSetById(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b) {
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    // 7、批量删除医院设置,ids来自于请求参数，并非来自路径
    @DeleteMapping("batchRemove")
    public Result batchRemoveHospitalSet(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    // 8、医院锁定和解锁的操作
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        // 设置医院的状态
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    // 9、发送签名秘钥
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        // 获取指定id的医院信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String hosname = hospitalSet.getHosname();
        String signKey = hospitalSet.getSignKey();

        // 发送信息
        return Result.ok();
    }

}

