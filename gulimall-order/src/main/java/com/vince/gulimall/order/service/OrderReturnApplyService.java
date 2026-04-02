package com.vince.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.common.utils.PageUtils;
import com.vince.gulimall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

