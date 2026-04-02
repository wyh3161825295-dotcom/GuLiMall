package com.vince.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vince.common.utils.PageUtils;
import com.vince.gulimall.product.entity.AttrGroupEntity;
import com.vince.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.vince.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

