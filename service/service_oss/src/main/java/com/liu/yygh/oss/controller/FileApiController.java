package com.liu.yygh.oss.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author lms
 * @date 2021-08-21 - 14:50
 */

@Api(tags = "OSS对象存储")
@RestController
@RequestMapping("/api/oss/file")
public class FileApiController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件到
     * @param file, MultipartFile用于接收上传的文件信息
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        // 将文件上传到阿里云的oss对象存储中
        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
