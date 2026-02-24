package geist.hegel.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Data
@TableName("sys_user")
public class SysUser implements UserDetails {
    @TableId
    private Long id;

    private String username;
    private String password;   // BCrypt hash
    private String roleCode;   // ADMIN/TEACHER/STUDENT
    private String avatarUrl;  // 头像 URL
    private boolean enabled;
    @TableField(exist = false)
    private Boolean queryEnabled;

    @TableField(exist = false)
    private int current;
    @TableField(exist = false)
    private int size;

    @TableField(exist = false)
    private MultipartFile avatarFile;

    // 不入库字段（MP2.2 用这个）
    @TableField(exist = false)
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
