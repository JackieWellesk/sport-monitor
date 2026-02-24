package geist.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import geist.hegel.entity.SportEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SportEventMapper extends BaseMapper<SportEvent> {
}