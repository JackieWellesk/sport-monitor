package geist.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import geist.hegel.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> { }
