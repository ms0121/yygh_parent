package com.liu.yygh.statistics.controller;

import com.liu.yygh.common.result.Result;
import com.liu.yygh.order.client.OrderFeignClient;
import com.lms.yygh.vo.order.OrderCountQueryVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lms
 * @date 2021-09-05 - 9:14
 * 需要设置相应的网关信息，然后在后台进行调用即可得到相应的数据信息
 * 但是在很多的场景下这是很有必要的一项工作
 */

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    // 注入远程调用接口
    @Resource
    private OrderFeignClient orderFeignClient;


    @GetMapping("getCountMap")
    public Result getCountMap(OrderCountQueryVo orderCountQueryVo){
        Map<String, Object> countMap = orderFeignClient.getCountMap(orderCountQueryVo);
        return Result.ok(countMap);
    }



}
