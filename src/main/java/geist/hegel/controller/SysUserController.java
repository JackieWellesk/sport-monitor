package geist.hegel.controller;

import com.baomidou.mybatisplus.plugins.Page;
import geist.hegel.common.R;
import geist.hegel.entity.SysUser;
import geist.hegel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    private final PasswordEncoder passwordEncoder;

    /**
     * 后端列表
     */
    @GetMapping("/list")
    public R<Page<SysUser>> page(@ModelAttribute SysUser cond) {
        return R.ok(sysUserService.queryPage(cond));
    }

    @PutMapping("/{userId}/{enabled}")
    public R<Object> update(@PathVariable Long userId, @PathVariable Boolean enabled) {
        SysUser sysUser = new SysUser();
        sysUser.setEnabled(enabled);
        sysUser.setId(userId);
        sysUserService.updateById(sysUser);
        return R.ok(null);
    }

    @DeleteMapping("{userId}")
    public R<Object> delete(@PathVariable Long userId) {
        sysUserService.deleteById(userId);
        return R.ok(null);
    }

    @GetMapping("info")
    public R<SysUser> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return R.ok(sysUserService.selectById(getUserId(auth.getPrincipal())));
    }

    @PutMapping(value = "update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<Object> update(@ModelAttribute SysUser sysUser) {
        try {
            SysUser currentUser = (SysUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String password = sysUser.getPassword();
            // 获取当前用户信息
            SysUser user = new SysUser();
            user.setId(sysUser.getId());

            // 如果提供了新密码，则更新密码并加密
            if (password != null && !password.isEmpty()) {
                String encodedPassword = passwordEncoder.encode(password);
                user.setPassword(encodedPassword);
            }

            // 保存更新后的用户信息
            user.setAvatarFile(sysUser.getAvatarFile());
            user.setEnabled(true);
            sysUserService.updateUserAndFileById(user);

            currentUser.setAvatarUrl(user.getAvatarUrl());
            currentUser.setPassword(user.getPassword());

            // 更新spring security中的信息
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                    currentUser,
                    null,
                    currentUser.getAuthorities() // 使用更新后的权限
            );
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

            return R.ok("信息已更新");
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("更新失败");
        }
    }

    private Long getUserId(Object principal) {
        if (principal instanceof SysUser) {
            SysUser res = (SysUser) principal;
            res.setPassword(null);
            return res.getId();
        }
        return null;
    }

}
