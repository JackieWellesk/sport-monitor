package geist.hegel.controller;

import geist.hegel.common.R;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserService sysUserService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public R<?> login(@RequestBody SysUser req, HttpServletRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 创建 session，让浏览器拿到 JSESSIONID
            request.getSession(true);

            return R.ok(safeUser(auth.getPrincipal()));
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
        if (StringUtils.isEmpty(req.getRoleCode())) return R.fail("角色值不能为空");

        UserDetails exist = sysUserService.selectByUsername(req.getUsername());
        if (exist != null) return R.fail("用户名已存在");

        req.setPassword(passwordEncoder.encode(req.getPassword()));
        req.setEnabled(true);

        sysUserService.insert(req);
        return R.ok("注册成功");
    }

    @GetMapping("/me")
    public R<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return R.ok(safeUser(auth.getPrincipal()));
    }

    @PostMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return R.ok("已退出登录");
    }

    private SysUser safeUser(Object principal) {
        SysUser copy = new SysUser();
        if (principal instanceof SysUser) {
            SysUser u = (SysUser) principal;
            copy.setId(u.getId());
            copy.setUsername(u.getUsername());
            copy.setRoleCode(u.getRoleCode());
            copy.setAvatarUrl(u.getAvatarUrl());
            copy.setEnabled(u.isEnabled());
            copy.setRoleCode(u.getRoleCode());
            return copy;
        }
        copy.setRoleCode(principal.toString());
        return copy;
    }

}
