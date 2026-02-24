package geist.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import geist.hegel.entity.SportEventResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SportEventResultMapper extends BaseMapper<SportEventResult> {

    List<SportEventResult> selectPageByCond(Page<SportEventResult> page, @Param("cond") SportEventResult cond);

}
