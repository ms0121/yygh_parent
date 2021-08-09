package com.liu.yygh.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.mapper.HospitalSetMapper;
import com.liu.yygh.service.HospitalSetService;
import com.lms.yygh.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

/**
 * @author lms
 * @date 2021-07-18 - 21:33
 * ServiceImpl： 已经向容器中注入了相关的baseMapper类，所以在实现类中不需要显示的去往容器注入mapper对象
 */
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet>
        implements HospitalSetService {
}
