package com.liu.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author lms
 * @date 2021-08-21 - 14:50
 */
public interface FileService {

    // 上传文件到阿里云的oss
    String upload(MultipartFile file);
}
