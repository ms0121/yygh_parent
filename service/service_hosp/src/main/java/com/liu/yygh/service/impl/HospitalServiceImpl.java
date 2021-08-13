package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.repository.HospitalRepository;
import com.liu.yygh.service.HospitalService;
import com.lms.yygh.model.hosp.Hospital;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author lms
 * @date 2021-08-11 - 15:16
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    // 实现数据在mongodb中的增删改查的操作
    @Resource
    private HospitalRepository hospitalRepository;

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

}














