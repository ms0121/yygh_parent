package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.cmn.client.DictFeignClient;
import com.liu.yygh.repository.HospitalRepository;
import com.liu.yygh.service.HospitalService;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.vo.hosp.HospitalQueryVo;
import org.bouncycastle.eac.EACCertificateBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-11 - 15:16
 * 医院的操作，要实现医院等级的查找，所以要实现远程调用
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    // 实现数据在mongodb中的增删改查的操作
    @Resource
    private HospitalRepository hospitalRepository;

    @Resource
    private DictFeignClient dictFeignClient;



    /**
     * 保存数据信息到MongoDB中
     * @param resultMap
     */
    @Override
    public void save(Map<String, Object> resultMap) {
        // 1. 将参数map集合转为对象Hospital
        // 首先使用fastJson将map数据转为字符串,然后将字符串转为相应的对象
        String jsonString = JSONObject.toJSONString(resultMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        // 2. 查询MongoDB数据库中是否存在该数据
        String hoscode = hospital.getHoscode();
        Hospital tempHospital = hospitalRepository.getHospitalByHoscode(hoscode);

        // 3.判断
        if (tempHospital != null){
            // 说明数据库中存在该信息，则为修改操作
            tempHospital.setUpdateTime(hospital.getUpdateTime());
            tempHospital.setIsDeleted(0);
            tempHospital.setStatus(hospital.getStatus());
            hospitalRepository.save(tempHospital);
        }else {
            // 如果不存在，则为添加操作
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    // 查询医院信息
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    // 条件查询分页
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        // 创建pageable对象，用于封装当前页码和每页显示的记录数
        Pageable pageable = PageRequest.of(page - 1, limit);
        // 创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                // 模糊匹配,忽略大小写
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        // 将前端传过来的查询条件封装在返回数据类型的对象中
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);

        // 创建对象,传入要查询的对象，以及匹配器
        Example<Hospital> example = Example.of(hospital, matcher);
        // 调用方法实现查询操作(pages包含了医院的信息，但是并没有相应的等级信息，所以还需要另外补上)
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);

        // 使用java8的新特性进行遍历列表的内容
        pages.getContent().stream().forEach(item -> {
            this.setHospitalHosType(item);
        });
        return pages;
    }

    // 设置医院的等级信息
    // 处查询list集合，遍历进行医院等级封装
    private void setHospitalHosType(Hospital hospital) {
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        hospital.getParam().put("hostypeString", hostypeString);
    }

}














