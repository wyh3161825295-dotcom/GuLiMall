package com.ncst.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncst.common.utils.PageUtils;
import com.ncst.gulimall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:36:38
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

