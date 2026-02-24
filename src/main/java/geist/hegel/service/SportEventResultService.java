package geist.hegel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import geist.hegel.entity.SportEvent;
import geist.hegel.entity.SportEventResult;

public interface SportEventResultService extends IService<SportEventResult> {
    Page<SportEventResult> queryPage(SportEventResult cond);
}