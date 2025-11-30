package com.example.health.web.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.AuditLog;
import com.example.health.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@Tag(name = "Admin Audit Logs")
public class AdminAuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @GetMapping
    @Operation(summary = "获取审计日志列表")
    public ApiResponse<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long adminUserId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Page<AuditLog> pageParam = new Page<>(page, size);
        Page<AuditLog> result = auditLogService.getAuditLogs(pageParam, adminUserId, action, resource, startDate, endDate);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取审计日志统计")
    public ApiResponse<Object> getAuditStats() {
        Object stats = auditLogService.getAuditStats();
        return ApiResponse.success(stats);
    }
}
