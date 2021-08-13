package com.liu.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lms.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author lms
 * @date 2021-08-09 - 14:06
 */
public interface DictService extends IService<Dict> {

    List<Dict> findChildData(Long id);

    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);

    String getDictName(String dictCode, String value);
}
