package com.vince.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.common.utils.PageUtils;
import com.vince.gulimall.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

