package com.example.health.web.admin;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.AdminUser;
import com.example.health.service.AdminUserService;
import com.example.health.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "Admin Auth")
public class AdminAuthController {
    private final AdminUserService adminUserService;
    private final JwtService jwtService;

    public AdminAuthController(AdminUserService adminUserService, JwtService jwtService) {
        this.adminUserService = adminUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public ApiResponse<?> adminLogin(@RequestBody AdminLoginRequest request) {
        try {
            // 验证用户名和密码
            AdminUser adminUser = adminUserService.findByUsername(request.getUsername());
            if (adminUser == null || !verifyPassword(request.getPassword(), adminUser.getPasswordHash())) {
                return ApiResponse.fail(401, "用户名或密码错误");
            }
            
            if (adminUser.getStatus() != 1) {
                return ApiResponse.fail(403, "账户已被禁用");
            }
            
            // 更新最后登录时间
            adminUser.setLastLoginAt(LocalDateTime.now());
            adminUserService.updateById(adminUser);
            
            // 生成JWT token
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("platform", "admin");
            claims.put("role", "ADMIN");
            String token = jwtService.generateToken(adminUser.getId(), "admin", claims);
            
            // 返回登录结果
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", adminUser);
            data.put("expiresIn", 7200); // 2小时
            
            return ApiResponse.ok(data);
        } catch (Exception e) {
            return ApiResponse.fail(500, "登录失败：" + e.getMessage());
        }
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前管理员信息")
    public ApiResponse<?> getCurrentAdmin() {
        // 这里应该从JWT token中获取当前管理员信息
        // 为了演示，返回模拟数据
        AdminUser adminUser = new AdminUser();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setDisplayName("系统管理员");
        adminUser.setStatus(1);
        
        return ApiResponse.ok(adminUser);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新token")
    public ApiResponse<?> refreshToken() {
        // 刷新token逻辑
        HashMap<String, Object> data = new HashMap<>();
        data.put("token", "new_token_here");
        data.put("expiresIn", 7200);
        
        return ApiResponse.ok(data);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public ApiResponse<?> logout() {
        // 退出登录逻辑（如需要，可以将token加入黑名单）
        return ApiResponse.ok("退出成功");
    }

    // 简单的密码验证（实际项目中应使用BCrypt等安全的哈希算法）
    private boolean verifyPassword(String inputPassword, String storedPassword) {
        // 由于数据库中存储的是明文密码，直接比较
        // 实际项目中应该使用BCrypt.checkpw(inputPassword, storedPassword)
        return inputPassword.equals(storedPassword);
    }

    @Data
    public static class AdminLoginRequest {
        private String username;
        private String password;
        private Boolean remember; // 记住我选项
    }
}
