package geist.hegel.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import geist.hegel.dto.RunMonthAgg;
import geist.hegel.dto.TrendMonthAgg;
import geist.hegel.entity.SportRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SportRecordMapper extends BaseMapper<SportRecord> {

    List<SportRecord> selectByUserAndTimeRange(@Param("userId") Long userId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    List<TrendMonthAgg> selectMonthAgg(@Param("userId") Long userId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    List<RunMonthAgg> selectRunMonthAgg(@Param("userId") Long userId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

}