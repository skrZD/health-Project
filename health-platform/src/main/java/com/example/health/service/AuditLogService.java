package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.AuditLog;
import com.example.health.infra.mapper.AuditLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService extends ServiceImpl<AuditLogMapper, AuditLog> {
    
    /**
     * 记录审计日志
     */
    public void logAction(Long adminUserId, String action, String resource, String resourceId, String detail, String ip) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAdminUserId(adminUserId);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setDetail(detail);
        auditLog.setIp(ip);
        auditLog.setCreatedAt(LocalDateTime.now());
        this.save(auditLog);
    }
    
    /**
     * 获取审计日志列表
     */
    public Page<AuditLog> getAuditLogs(Page<AuditLog> page, Long adminUserId, String action, String resource, String startDate, String endDate) {
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();
        
        if (adminUserId != null) {
            queryWrapper.eq("admin_user_id", adminUserId);
        }
        
        if (action != null && !action.trim().isEmpty()) {
            queryWrapper.like("action", action);
        }
        
        if (resource != null && !resource.trim().isEmpty()) {
            queryWrapper.like("resource", resource);
        }
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            queryWrapper.ge("created_at", startDate);
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryWrapper.le("created_at", endDate);
        }
        
        queryWrapper.orderByDesc("created_at");
        return this.page(page, queryWrapper);
    }
    
    /**
     * 获取审计日志统计
     */
    public Object getAuditStats() {
        // 这里可以添加统计逻辑
        return null;
    }
}
