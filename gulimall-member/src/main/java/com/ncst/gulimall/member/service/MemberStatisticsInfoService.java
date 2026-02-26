package com.ncst.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ncst.common.utils.PageUtils;
import com.ncst.gulimall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:34:53
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

