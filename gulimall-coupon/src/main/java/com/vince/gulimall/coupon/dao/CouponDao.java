package com.vince.gulimall.coupon.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vince.gulimall.coupon.entity.CouponEntity;
import org.apache.ibatis.annotations.Mapper;

//优惠券信息
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
