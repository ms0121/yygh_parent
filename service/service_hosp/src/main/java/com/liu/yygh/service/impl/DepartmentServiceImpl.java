package com.liu.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liu.yygh.repository.DepartmentRepository;
import com.liu.yygh.service.DepartmentService;
import com.lms.yygh.model.hosp.Department;
import com.lms.yygh.vo.hosp.DepartmentQueryVo;
import com.lms.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lms
 * @date 2021-08-12 - 23:01
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Resource
    private DepartmentRepository departmentRepository;


    /**
     * 上传(添加)科室操作
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        // 1. 将参数map集合转为对象Department
        // 首先使用fastJson将map数据转为字符串,然后将字符串转为相应的对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        // 2.查询mongodb数据库中是否存在该科室信息
        Department tempDepartment =
                departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(), department.getDepcode());

        // 判断当前的Department是修改还是添加
        if (tempDepartment != null){
            tempDepartment.setUpdateTime(new Date());
            tempDepartment.setIsDeleted(0);
            departmentRepository.save(tempDepartment);
        }else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    /**
     * 科室列表查询操作：
     * 注意，当前的查询操作时直接从MongoDB数据库中查询科室的信息，所以要编写相应的分页查询语句
     * @param page 当前页码
     * @param limit 每页的记录数
     * @param departmentQueryVo 查询条件
     * @return
     */
    @Override
    public Page<Department> selectPage(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {
        // 1. 构建分页查询
        // 将查询对象DepartmentQueryVo中的数据赋值到Department中
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo, department);
        department.setIsDeleted(0);

        // 设置排序的时间
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        // 0为第一页，构建分页对象
        Pageable pageAble = PageRequest.of(page - 1, limit, sort);

        // 创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() // 构建匹配对象
                                 // 改变默认字符串的匹配方式:模糊匹配
                                 .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                 //改变默认大小写忽略方式：忽略大小写
                                 .withIgnoreCase(true);
        // 创建实例(泛型表示查询之后要返回的数据类型)
        Example<Department> example = Example.of(department, matcher);

        // 2.执行查询的操作
        Page<Department> all = departmentRepository.findAll(example, pageAble);
        return all;
    }

    /**
     * 科室删除操作
     * @param hoscode
     * @param depcode
     */
    @Override
    public void remove(String hoscode, String depcode) {
        // 根据医院编号和科室编号从mongodb中查询科室信息
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    /**
     * 根据医院编号，查询医院的所有科室列表
      * @param hoscode
     * @return
     */
    @Override
    public List<DepartmentVo> getDeptList(String hoscode) {
        // 创建list集合，用于最终数据的封装
        List<DepartmentVo> result = new ArrayList<>();

        // 根据医院编号进行条件查询所有的科室信息，从mongodb中查询所有的科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        // 构建查询的条件
        Example<Department> example = Example.of(departmentQuery);
        // 所有科室的列表信息
        List<Department> departmentList = departmentRepository.findAll(example);

        // 根据大科室编号，bigcode 分组，获取每个大科室里面下级的子科室
        // 使用java8的新特性进行分组，但是不改变原内容(返回的结果Map中的string代表大科室的编号，
        // list为该编号对应的所有的小科室信息)
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));

        // 遍历map集合 departmentMap(将每个医院编号和对应的小科室信息设置在DepartmentVo对象中，进而设置在list列表中)
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            // 分别获取(大科室编号)key和value(小科室列表)值
            String bigCode = entry.getKey();
            List<Department> departmentList1 = entry.getValue();

            // 封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigCode);
            departmentVo.setDepname(departmentList1.get(0).getDepname());

            // 封装小科室(小科室存放在list中，然后设置在对应大科室的children属性中)
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : departmentList1) {
                DepartmentVo departmentVo1 = new DepartmentVo();
                departmentVo1.setDepcode(department.getDepcode());
                departmentVo1.setDepname(department.getDepname());
                // 封装到list集合中
                children.add(departmentVo1);
            }

            // 把小科室的list集合设置到大科室children里面
            departmentVo.setChildren(children);

            // 将每个大科室和对应的小科室放到最终的result列表中
            result.add(departmentVo);
        }
        return result;
    }

    // 根据医院编号和科室编号获取科室的名称
    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            return department.getDepname();
        }
        return null;
    }


    // 根据科室编号和医院编号，查询科室信息
    @Override
    public Department getDepartment(String hoscode, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
    }
}
