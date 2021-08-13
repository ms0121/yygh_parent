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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    /**
     * ServiceImpl已经在容器中注入了BaseMapper，所以在service的实现类中就可以直接使用BaseMapper
     * 根据数据id查询子数据列表
     * 将该方法查询得到的结果放置在缓存中，下次再来查询的时候，直接从缓存中拿取数据，如果缓存中没有数据信息
     * 将会执行该方法，并将结果放置在缓存中
     *
     * 添加缓存的步骤：
     *  1.添加缓存的相关依赖
     *  2.添加redis的配置类
     *  3.在业务方法中(服务)添加redis配置
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = "dict", keyGenerator = "keyGenerator")
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
     * allEntries = true: 方法调用后清空所有缓存
     *
     */
    @CacheEvict(value = "dict", allEntries=true)
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

    // 查询dictName
    @Override
    public String getDictName(String dictCode, String value) {
        // 如果dictCode为空，直接根据value进行查询
        if (StringUtils.isEmpty(dictCode)){
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value", value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        } else {
            // dict不为空，根据dictCode和value进行查询
            // 首先根据dictCode查出dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parentId = codeDict.getId();

            // 根据parentId和value值进行查询
            Dict dict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentId)
                    .eq("value", value));
            return dict.getName();
        }
    }

    // 根据dictCode查询dict对象
    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode);
        return baseMapper.selectOne(wrapper);
    }


    // 判断该id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
