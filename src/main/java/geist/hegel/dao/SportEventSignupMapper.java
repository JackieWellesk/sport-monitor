package geist.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import geist.hegel.entity.SportEventSignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SportEventSignupMapper extends BaseMapper<SportEventSignup> {
    List<SportEventSignup> selectPageByCond(Pagination page, @Param("cond") SportEventSignup cond);
}