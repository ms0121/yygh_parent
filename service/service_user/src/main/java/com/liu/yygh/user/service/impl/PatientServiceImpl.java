package com.liu.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.cmn.client.DictFeignClient;
import com.liu.yygh.user.mapper.PatientMapper;
import com.liu.yygh.user.service.PatientService;
import com.lms.yygh.enums.DictEnum;
import com.lms.yygh.model.user.Patient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-23 - 21:45
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    // 在数据字典中查询就诊人的信息
    // 使用远程的调用的方法，从dict中查询信息
    @Resource
    private DictFeignClient dictFeignClient;

    // 根据当前登录用户的ID信息来获取病人信息
    @Override
    public List<Patient> findAll(Long userId) {
        // 构建查询语句
        QueryWrapper<Patient> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        List<Patient> patients = baseMapper.selectList(query);

        // 通过远程调用，得到编码对应的具体内容，查询数据字典中的内容
        for (Patient item : patients) {// 封装部分需要的信息在每个patient中
            this.packagePatient(item);
        }
        return patients;
    }

    // 查询就诊人信息
    @Override
    public Patient getPatientById(Long id) {
        Patient patient = baseMapper.selectById(id);
        // 封装就诊人信息
        return this.packagePatient(patient);
    }

    //Patient对象里面其他参数封装
    private Patient packagePatient(Patient patient) {
        // 根据证件类型编码，获取具体的证件类型
        String certificatesTypeString = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getCertificatesType());
        // 联系人证件信息
        String contactsCertificatesTypeString  = dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),
                patient.getContactsCertificatesType());
        // 获取省份的信息
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        // 市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        // 将需要返回的数据信息设置在patient的param属性中
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
