package com.ncst.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncst.common.utils.PageUtils;
import com.ncst.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:36:38
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

