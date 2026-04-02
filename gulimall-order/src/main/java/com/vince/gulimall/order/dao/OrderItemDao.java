package com.vince.gulimall.order.dao;

import com.vince.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
