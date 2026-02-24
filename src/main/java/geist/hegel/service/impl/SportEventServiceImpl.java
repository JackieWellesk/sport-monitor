package geist.hegel.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import geist.hegel.dao.SportEventMapper;
import geist.hegel.dao.SportEventSignupMapper;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportEventSignup;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SportEventService;
import geist.hegel.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SportEventServiceImpl
        extends ServiceImpl<SportEventMapper, SportEvent>
        implements SportEventService {

    @Resource
    private SportEventSignupMapper signupMapper;

    @Override
    public Page<SportEvent> queryPage(SportEvent cond) {
        Page<SportEvent> page = new Page<>(cond.getCurrent(), cond.getSize());
        EntityWrapper<SportEvent> queryWrapper = new EntityWrapper<>();
        SysUser userSafe = UserUtils.getUserSafe();
        if (userSafe != null && userSafe.getRoleCode().equals("TEACHER")) {
            queryWrapper.eq("created_by", userSafe.getId());
        }
        if (StringUtils.isNotBlank(cond.getTitle())) {
            queryWrapper.like("title", cond.getTitle());
        }
        if (userSafe != null && userSafe.getRoleCode().equals("STUDENT")) {
            queryWrapper.notIn("status", "DRAFT");
        }
        if (StringUtils.isNotBlank(cond.getStatus())) {
            queryWrapper.eq("status", cond.getStatus());
        }
        queryWrapper.orderBy("created_at", false);
        return this.selectPage(page, queryWrapper);
    }

    @Override
    public SportEvent selectInfoById(Long id) {
        SysUser userSafe = UserUtils.getUserSafe();
        SportEvent event = baseMapper.selectById(id);
        // 角色为学生时，需查询已经报名人数，自己是否已经报过名

        if (userSafe != null && userSafe.getRoleCode().equals("STUDENT")) {
            EntityWrapper<SportEventSignup> cond = new EntityWrapper<>();
            cond.eq("event_id", event.getId());
            cond.eq("status", "SIGNED");
            Long signupNumber = Long.valueOf(signupMapper.selectCount(cond));
            event.setSignupNumber(signupNumber);
            cond.eq("user_id", userSafe.getId());
            Boolean hasSignup = signupMapper.selectCount(cond) > 0;
            event.setHasSignup(hasSignup);
        }
        return event;
    }
}