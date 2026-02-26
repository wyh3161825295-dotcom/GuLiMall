package com.ncst.gulimall.order.dao;

import com.ncst.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:36:38
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
