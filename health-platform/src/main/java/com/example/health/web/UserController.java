package com.example.health.web;

import com.example.health.common.api.ApiResponse;
import com.example.health.common.util.SecurityUtils;
import com.example.health.domain.entity.User;
import com.example.health.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<User> getCurrentUser() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        User user = userService.getById(currentUserId);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        return ApiResponse.ok(user);
    }

    @GetMapping("/me/stats")
    @Operation(summary = "获取当前用户统计信息")
    public ApiResponse<Map<String, Object>> getCurrentUserStats() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        // 这里可以添加统计逻辑，比如总运动天数、总消耗卡路里等
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", currentUserId);
        stats.put("totalDays", 0); // 需要从运动记录中计算
        stats.put("totalCalories", 0); // 需要从运动记录中计算
        stats.put("totalMinutes", 0); // 需要从运动记录中计算
        
        return ApiResponse.ok(stats);
    }

    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public ApiResponse<User> updateCurrentUser(@RequestBody User user) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        User existing = userService.getById(currentUserId);
        if (existing == null) {
            return ApiResponse.fail(404, "用户不存在");
        }
        
        // 只更新允许修改的字段
        existing.setName(user.getName());
        existing.setAge(user.getAge());
        existing.setHeight(user.getHeight());
        existing.setWeight(user.getWeight());
        existing.setGender(user.getGender());
        existing.setUpdatedAt(LocalDateTime.now());
        
        userService.updateById(existing);
        return ApiResponse.ok(existing);
    }

    @PutMapping("/me/goals")
    @Operation(summary = "更新当前用户目标")
    public ApiResponse<Map<String, Object>> updateCurrentUserGoals(
            @RequestBody Map<String, Object> goals
    ) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        User user = userService.getById(currentUserId);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        // 更新用户目标字段
        if (goals.containsKey("dailyCalorieGoal")) {
            user.setDailyCalorieGoal((Integer) goals.get("dailyCalorieGoal"));
        }
        if (goals.containsKey("dailyExerciseGoal")) {
            user.setDailyExerciseGoal((Integer) goals.get("dailyExerciseGoal"));
        }
        if (goals.containsKey("dailyStepGoal")) {
            user.setDailyStepGoal((Integer) goals.get("dailyStepGoal"));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userService.updateById(user);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "目标更新成功");
        result.put("goals", goals);
        
        return ApiResponse.ok(result);
    }

    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public ApiResponse<Map<String, String>> uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        // 这里应该实现文件上传逻辑，保存到文件系统或云存储
        // 目前返回模拟数据
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", "/static/avatars/" + currentUserId + ".jpg");
        result.put("message", "头像上传成功");
        
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情")
    public ApiResponse<User> getUser(@PathVariable("id") Long id) {
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
        user.setUpdatedAt(LocalDateTime.now());
        userService.updateById(user);
        return ApiResponse.ok(user);
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "查询用户统计信息")
    public ApiResponse<Map<String, Object>> getUserStats(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        // 这里可以添加统计逻辑，比如总运动天数、总消耗卡路里等
        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", id);
        stats.put("totalDays", 0); // 需要从运动记录中计算
        stats.put("totalCalories", 0); // 需要从运动记录中计算
        stats.put("totalMinutes", 0); // 需要从运动记录中计算
        
        return ApiResponse.ok(stats);
    }

    @PutMapping("/{id}/goals")
    @Operation(summary = "更新用户目标")
    public ApiResponse<Map<String, Object>> updateUserGoals(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> goals
    ) {
        User user = userService.getById(id);
        if (user == null) {
            return ApiResponse.fail(404, "用户不存在");
        }

        // 这里可以将目标存储到用户表或单独的目标表
        // 目前只是返回成功，实际实现需要根据具体需求调整
        Map<String, Object> result = new HashMap<>();
        result.put("message", "目标更新成功");
        result.put("goals", goals);
        
        return ApiResponse.ok(result);
    }
}