package geist.hegel.utils;

import geist.hegel.entity.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    public static SysUser getUserSafe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof SysUser) {
            SysUser u = (SysUser) principal;

            SysUser copy = new SysUser();
            copy.setId(u.getId());
            copy.setUsername(u.getUsername());
            copy.setRoleCode(u.getRoleCode());
            copy.setAvatarUrl(u.getAvatarUrl());
            copy.setEnabled(u.isEnabled());
            return copy;
        }
        return null;
    }

    public static Long getUserId() {
        SysUser userSafe = getUserSafe();
        if (userSafe == null) return null;
        return userSafe.getId();
    }

}
