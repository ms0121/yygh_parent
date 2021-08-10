package com.liu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.cmn.listener.DictListener;
import com.liu.yygh.cmn.mapper.DictMapper;
import com.liu.yygh.cmn.service.DictService;
import com.lms.yygh.model.cmn.Dict;
import com.lms.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-09 - 14:07
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    // ServiceImpl已经在容器中注入了BaseMapper，所以在service的实现类中就可以直接使用BaseMapper
    // 根据数据id查询子数据列表
    @Override
    public List<Dict> findChildData(Long id) {
        // 使用该对象来构建sql语句的
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        // 条件： parent_id = #{id}
        wrapper.eq("parent_id", id);
        // 调用接口进行查询
        List<Dict> dictList = baseMapper.selectList(wrapper);
        // 想list集合中的每个dict对象设置hasChildren值(树形结构特有的属性)，目的就是为了实现树形结构
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            // 判断当前对象是否是Child（即该id下面是否还有子节点）
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    /**
     * 导出数据字典到Excel中去
     * @param response
     */
    @Override
    public void exportData(HttpServletResponse response) {
        try {

            // 设置返回客户端的contentType和编码方式
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");

            // 设置文件名
            String fileName = "dict";
            // 设置相应的header信息
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

            // 查询所有的数据字典数据信息，并将其封装到list中，从而进行导出的操作
            List<Dict> dictList = baseMapper.selectList(null);
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for(Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                // 将dict中的属性赋值到dictvo中，等价于下面这一句
                // dictVo.setId(dict.getId());
                BeanUtils.copyProperties(dict, dictVo);
                dictVoList.add(dictVo);
            }

            // 将数据进行导出
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                    .sheet("数据字典")
                    .doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输入导入功能
     * @param file 导入的文件名，
     *   文件输入流，导入的数据类型，以及监听器对象
     */
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper))
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 判断该id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
