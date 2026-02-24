package geist.hegel.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import geist.hegel.dao.SportEventResultMapper;
import geist.hegel.entity.SportEventResult;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SportEventResultService;
import geist.hegel.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportEventResultServiceImpl
        extends ServiceImpl<SportEventResultMapper, SportEventResult>
        implements SportEventResultService {

    @Override
    public Page<SportEventResult> queryPage(SportEventResult cond) {
        Page<SportEventResult> page = new Page<>(cond.getCurrent(), cond.getSize());
        SysUser userSafe = UserUtils.getUserSafe();
        if (userSafe != null && userSafe.getRoleCode() != null && userSafe.getRoleCode().equals("TEACHER")) {
            cond.setEventCreatorId(userSafe.getId());
        }
        if (userSafe != null && userSafe.getRoleCode() != null && userSafe.getRoleCode().equals("STUDENT")) {
            cond.setUserId(userSafe.getId());
        }
        return page.setRecords(baseMapper.selectPageByCond(page, cond));
    }
}