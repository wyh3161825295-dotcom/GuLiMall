package com.ncst.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncst.common.utils.PageUtils;
import com.ncst.gulimall.coupon.entity.CouponSpuCategoryRelationEntity;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:33:06
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

