package com.liu.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.cmn.Dict;

import java.util.List;

/**
 * @author lms
 * @date 2021-08-09 - 14:06
 */
public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);
}
