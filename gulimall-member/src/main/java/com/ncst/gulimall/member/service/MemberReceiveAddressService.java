package com.ncst.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncst.common.utils.PageUtils;
import com.ncst.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:34:53
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

