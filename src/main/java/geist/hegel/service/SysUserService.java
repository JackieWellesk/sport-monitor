package geist.hegel.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import geist.hegel.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Map;

public interface SysUserService extends IService<SysUser>, UserDetailsService {

    Page<SysUser> queryPage(SysUser cond);

    UserDetails selectByUsername(String username);

    void updateUserAndFileById(SysUser sysUser) throws IOException;
}
