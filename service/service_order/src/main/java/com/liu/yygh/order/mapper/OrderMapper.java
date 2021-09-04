package com.liu.yygh.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lms.yygh.model.order.OrderInfo;
import com.lms.yygh.vo.order.OrderCountQueryVo;
import com.lms.yygh.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lms
 * @date 2021-08-27 - 17:29
 */
public interface OrderMapper extends BaseMapper<OrderInfo> {

    // 查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
