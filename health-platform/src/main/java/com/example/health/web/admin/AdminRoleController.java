package com.example.health.web.admin;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.Role;
import com.example.health.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "Admin Roles")
public class AdminRoleController {
    
    @Autowired
    private RoleService roleService;
    
    @GetMapping
    @Operation(summary = "获取角色列表")
    public ApiResponse<List<Role>> getRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResponse.success(roles);
    }
    
    @PostMapping
    @Operation(summary = "创建角色")
    public ApiResponse<Role> createRole(@RequestBody Role role) {
        Role created = roleService.createRole(role);
        return ApiResponse.success(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        Role updated = roleService.updateRole(role);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        boolean success = roleService.deleteRole(id);
        return success ? ApiResponse.success() : ApiResponse.error("删除失败");
    }
}
