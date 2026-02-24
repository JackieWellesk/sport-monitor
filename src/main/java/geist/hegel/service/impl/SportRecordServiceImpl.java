package geist.hegel.service.impl;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import geist.hegel.dao.SportRecordMapper;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportRecord;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SportRecordService;
import geist.hegel.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SportRecordServiceImpl
        extends ServiceImpl<SportRecordMapper, SportRecord>
        implements SportRecordService {

    @Override
    public Page<SportRecord> queryPage(SportRecord cond) {
        Page<SportRecord> page = new Page<>(cond.getCurrent(), cond.getSize());
        Wrapper<SportRecord> queryWrapper = new EntityWrapper<>();
        SysUser userSafe = UserUtils.getUserSafe();
        if (userSafe != null && userSafe.getRoleCode().equals("STUDENT")) {
            queryWrapper.eq("user_id", userSafe.getId());
        }
        if (StringUtils.isNotBlank(cond.getSportType())) {
            queryWrapper.eq("sport_type", cond.getSportType());
        }

        return selectPage(page, queryWrapper);
    }

}