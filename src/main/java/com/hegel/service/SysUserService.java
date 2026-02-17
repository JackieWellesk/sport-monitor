package com.hegel.service;

import com.baomidou.mybatisplus.service.IService;
import com.hegel.entity.SysUser;
import org.springframework.stereotype.Service;

@Service
public interface SysUserService extends IService<SysUser> {

    SysUser getUserByName(String username);

}
