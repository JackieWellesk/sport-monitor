package geist.hegel.service.impl;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import geist.hegel.dao.SportEventSignupMapper;
import geist.hegel.entity.SportEventSignup;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SportEventSignupService;
import geist.hegel.utils.UserUtils;
import org.springframework.stereotype.Service;

@Service
public class SportEventSignupServiceImpl
        extends ServiceImpl<SportEventSignupMapper, SportEventSignup>
        implements SportEventSignupService {
    @Override
    public Page<SportEventSignup> queryPage(SportEventSignup cond) {
        Page<SportEventSignup> page = new Page<>(cond.getCurrent(), cond.getSize());
        SysUser user = UserUtils.getUserSafe();
        if (user != null && user.getRoleCode().equals("STUDENT")) {
            cond.setUserId(UserUtils.getUserId());
        }
        if (user != null && user.getRoleCode().equals("TEACHER")) {
            cond.setTeacherId(UserUtils.getUserId());
        }
        return page.setRecords(baseMapper.selectPageByCond(page, cond));
    }
}
