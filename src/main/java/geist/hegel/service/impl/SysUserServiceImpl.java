package geist.hegel.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import geist.hegel.dao.SysUserMapper;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

import static geist.hegel.utils.FileUtils.deleteFile;
import static geist.hegel.utils.FileUtils.upload;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser u = this.selectOne(new EntityWrapper<SysUser>().eq("username", username));
        if (u == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 单角色：Spring Security
        u.setAuthorities(Collections.singletonList(
                new SimpleGrantedAuthority(u.getRoleCode())
        ));
        return u; // ✅ 直接返回实体本身
    }

    @Override
    public Page<SysUser> queryPage(SysUser cond) {
        Page<SysUser> page = new Page<>(cond.getCurrent(), cond.getSize());
        EntityWrapper<SysUser> queryWrapper = new EntityWrapper<>();
        if (StringUtils.isNotBlank(cond.getUsername())) {
            queryWrapper.like("username", cond.getUsername());
        }
        if (StringUtils.isNotBlank(cond.getRoleCode())) {
            queryWrapper.eq("role_code", cond.getRoleCode());
        }
        if (cond.getQueryEnabled() != null) {
            queryWrapper.eq("enabled", cond.getQueryEnabled());
        }
        return this.selectPage(page, queryWrapper);
    }

    @Override
    public UserDetails selectByUsername(String username) {
        return this.selectOne(new EntityWrapper<SysUser>().eq("username", username));
    }

    @Override
    public void updateUserAndFileById(SysUser sysUser) throws IOException {
        SysUser origin = this.selectById(sysUser.getId());
        if (sysUser.getAvatarFile() != null) {
            deleteFile(origin.getAvatarUrl());
            String fileName = upload(sysUser.getAvatarFile());
            sysUser.setAvatarUrl(fileName);
        }
        baseMapper.updateById(sysUser);
    }

}
