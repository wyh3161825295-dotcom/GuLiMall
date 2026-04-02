package com.vince.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.common.utils.PageUtils;
import com.vince.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

