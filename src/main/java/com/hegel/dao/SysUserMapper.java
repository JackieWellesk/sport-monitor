package com.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hegel.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> { }
