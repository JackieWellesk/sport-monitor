package com.hegel.controller;

import com.hegel.common.R;
import com.hegel.entity.SysUser;
import com.hegel.security.LoginUser;
import com.hegel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Resource
    private SysUserService sysUserService;




    @PostMapping("/login")
    public R<?> login(@RequestBody SysUser req, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());

            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 关键：创建 session，让浏览器拿到 JSESSIONID
            request.getSession(true);

            return R.ok(meData(auth));
        } catch (BadCredentialsException e) {
            return R.fail("账号或密码错误");
        } catch (DisabledException e) {
            return R.fail("账号已禁用");
        }
    }

    @PostMapping("/register")
    public R<?> register(@RequestBody SysUser req) {
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) return R.fail("用户名不能为空");
        if (req.getPassword() == null || req.getPassword().length() < 6) return R.fail("密码至少6位");

        SysUser exist = sysUserService.getUserByName(req.getUsername());
        if (exist != null) return R.fail("用户名已存在");

        SysUser u = new SysUser();
        u.setUsername(req.getUsername());
        u.setPassword(req.getPassword());
        u.setEnabled(1);
        u.setAvatarUrl(req.getAvatarUrl());
        u.setRoleCode((req.getRoleCode() == null || req.getRoleCode().trim().isEmpty()) ? "STUDENT" : req.getRoleCode());

        sysUserService.insert(req);
        return R.ok("注册成功");
    }

    @GetMapping("/me")
    public R<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return R.ok(meData(auth));
    }

    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return R.ok("已退出登录");
    }

    private Map<String, Object> meData(Authentication auth) {
        Map<String, Object> m = new HashMap<>();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            m.put("authenticated", false);
            return m;
        }

        m.put("authenticated", true);

        Object p = auth.getPrincipal();
        if (p instanceof LoginUser) {
            LoginUser u = (LoginUser) p;
            m.put("username", u.getUsername());
            m.put("role", u.getRoleCode());
            m.put("avatarUrl", u.getAvatarUrl());
        } else {
            m.put("username", String.valueOf(p));
        }
        return m;
    }
}
