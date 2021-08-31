package com.liu.yygh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.common.exception.YyghException;
import com.liu.yygh.common.result.ResultCodeEnum;
import com.liu.yygh.mapper.HospitalSetMapper;
import com.liu.yygh.service.HospitalSetService;
import com.lms.yygh.model.hosp.Hospital;
import com.lms.yygh.model.hosp.HospitalSet;
import com.lms.yygh.vo.order.SignInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-07-18 - 21:33
 * ServiceImpl： 已经向容器中注入了相关的baseMapper类，所以在实现类中不需要显示的去往容器注入mapper对象
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {

    // 根据传递过来的医院编码，查询数据库，查看相应的签名信息
    @Override
    public String getSignKey(String hoscode) {
        // QueryWrapper<HospitalSet> 泛型为查询返回的数据类型
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }

    // 根据医院编号获取医院的签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        // 根据医院编号查询医院设置信息
        QueryWrapper<HospitalSet> query = new QueryWrapper<>();
        query.eq("hoscode", hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(query);
        if (null == hospitalSet){
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        // 设置返回的签名信息
        SignInfoVo signInfoVo = new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        System.out.println("hospitalSet.getApiUrl() = " + hospitalSet.getApiUrl());
        return signInfoVo;
    }
}
