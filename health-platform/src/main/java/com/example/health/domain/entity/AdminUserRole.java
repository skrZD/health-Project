package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_user_roles")
public class AdminUserRole {
    private Long adminUserId;
    private Long roleId;
}
