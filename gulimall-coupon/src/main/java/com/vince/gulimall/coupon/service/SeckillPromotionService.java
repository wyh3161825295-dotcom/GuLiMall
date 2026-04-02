package com.vince.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.common.utils.PageUtils;
import com.vince.gulimall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

