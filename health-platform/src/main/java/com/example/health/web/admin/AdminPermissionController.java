package com.example.health.web.admin;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.Permission;
import com.example.health.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "Admin Permissions")
public class AdminPermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    @Operation(summary = "获取权限列表")
    public ApiResponse<List<Permission>> getPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }
    
    @PostMapping
    @Operation(summary = "创建权限")
    public ApiResponse<Permission> createPermission(@RequestBody Permission permission) {
        Permission created = permissionService.createPermission(permission);
        return ApiResponse.success(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public ApiResponse<Permission> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        Permission updated = permissionService.updatePermission(permission);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public ApiResponse<Void> deletePermission(@PathVariable Long id) {
        boolean success = permissionService.deletePermission(id);
        return success ? ApiResponse.success() : ApiResponse.error("删除失败");
    }
}
