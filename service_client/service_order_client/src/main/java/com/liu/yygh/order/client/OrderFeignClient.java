package com.liu.yygh.order.client;

import com.lms.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author lms
 * @date 2021-09-05 - 9:04
 *
 * 开启FeignClient远程调用接口
 */
@FeignClient(value = "service-order")
@Repository
public interface OrderFeignClient {

    /**
     * @param orderCountQueryVo 当前传入的是一个json的数据信息
     * @return
     */
    @PostMapping("/api/order/orderInfo/inner/getCountMap")
    Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);

}
