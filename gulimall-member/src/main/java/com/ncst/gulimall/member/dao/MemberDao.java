package com.ncst.gulimall.member.dao;

import com.ncst.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2026-02-26 12:34:54
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
