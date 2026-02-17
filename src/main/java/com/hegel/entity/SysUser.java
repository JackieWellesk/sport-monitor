package com.hegel.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId
    private Long id;

    private String username;
    private String password;   // BCrypt hash
    private String roleCode;   // ADMIN/TEACHER/STUDENT
    private String avatarUrl;  // 头像 URL
    private Integer enabled;   // 1/0
}
