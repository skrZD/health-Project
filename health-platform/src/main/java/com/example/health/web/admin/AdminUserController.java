package com.example.health.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.User;
import com.example.health.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Admin Users")
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取用户列表")
    public ApiResponse<Page<User>> getUserList(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(User::getNickname, keyword).or().like(User::getOpenid, keyword);
        }
        if (gender != null && !gender.isEmpty()) {
            qw.eq(User::getGender, gender);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(User::getCreatedAt, startDate + "T00:00:00");
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(User::getCreatedAt, endDate + "T23:59:59");
        }
        
        qw.orderByDesc(User::getCreatedAt);
        
        Page<User> result = userService.page(Page.of(page, size), qw);
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public ApiResponse<User> getUserDetail(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        return ApiResponse.ok(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public ApiResponse<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User existing = userService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        
        user.setId(id);
        userService.updateById(user);
        return ApiResponse.ok(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public ApiResponse<Void> deleteUser(@PathVariable("id") Long id) {
        User existing = userService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        
        userService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户统计")
    public ApiResponse<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总用户数
        stats.put("totalUsers", userService.count());
        
        // 获取今日新增用户数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("newUsersToday", userService.lambdaQuery()
                .ge(User::getCreatedAt, todayStart)
                .le(User::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.ok(stats);
    }
}
