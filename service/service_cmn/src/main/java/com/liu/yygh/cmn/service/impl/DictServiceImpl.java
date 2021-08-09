package com.liu.yygh.cmn.service.impl;

import ch.qos.logback.core.pattern.ConverterUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.yygh.cmn.mapper.DictMapper;
import com.liu.yygh.cmn.service.DictService;
import com.lms.yygh.model.cmn.Dict;
import org.springframework.stereotype.Service;

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

    // 判断该id下面是否有子节点
    private boolean isChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
