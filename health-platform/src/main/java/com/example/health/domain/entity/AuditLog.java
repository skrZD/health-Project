package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("admin_user_id")
    private Long adminUserId;
    
    private String action;
    private String resource;
    
    @TableField("resource_id")
    private String resourceId;
    
    private String detail;
    private String ip;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
