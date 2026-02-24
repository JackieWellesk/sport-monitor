package geist.hegel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SysUser;

public interface SportEventService extends IService<SportEvent> {
    Page<SportEvent> queryPage(SportEvent cond);

    SportEvent selectInfoById(Long id);
}