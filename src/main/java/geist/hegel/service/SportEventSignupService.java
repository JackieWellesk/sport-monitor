package geist.hegel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import geist.hegel.entity.SportEventSignup;

public interface SportEventSignupService extends IService<SportEventSignup> {
    Page<SportEventSignup> queryPage(SportEventSignup cond);
}
