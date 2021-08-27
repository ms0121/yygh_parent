package com.liu.yygh.cmn.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author lms
 * @date 2021-08-14 - 9:52
 *
 */
// 表示调用注册中心的service-cmn微服务中的方法
@FeignClient("service-cmn")
@Repository
public interface DictFeignClient {

    // 只需要配置微服务向外暴露的接口名称即可
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value);

    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value") String value);

}
