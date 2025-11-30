package com.example.health.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.ExerciseRecord;
import com.example.health.domain.entity.User;
import com.example.health.service.ExerciseRecordService;
import com.example.health.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/exercise-records")
@Tag(name = "Admin Exercise Record Management")
public class AdminExerciseRecordController {
    private final ExerciseRecordService exerciseRecordService;
    private final UserService userService;

    public AdminExerciseRecordController(ExerciseRecordService exerciseRecordService, UserService userService) {
        this.exerciseRecordService = exerciseRecordService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取运动记录列表（支持搜索、分页、筛选）")
    public ApiResponse<Page<Map<String, Object>>> getExerciseRecordList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "exerciseType", required = false) String exerciseType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        LambdaQueryWrapper<ExerciseRecord> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(ExerciseRecord::getNote, keyword);
        }
        if (userId != null) {
            qw.eq(ExerciseRecord::getUserId, userId);
        }
        if (exerciseType != null && !exerciseType.isEmpty()) {
            qw.eq(ExerciseRecord::getExerciseType, exerciseType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(ExerciseRecord::getExerciseTime, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(ExerciseRecord::getExerciseTime, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        qw.orderByDesc(ExerciseRecord::getExerciseTime);

        Page<ExerciseRecord> result = exerciseRecordService.page(Page.of(page, size), qw);

        // 获取用户信息并构建返回数据
        List<Long> userIds = result.getRecords().stream()
                .map(ExerciseRecord::getUserId)
                .distinct()
                .collect(Collectors.toList());

        final Map<Long, User> userMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userService.listByIds(userIds);
            userMap.putAll(users.stream().collect(Collectors.toMap(User::getId, user -> user)));
        }

        // 构建返回数据
        List<Map<String, Object>> records = result.getRecords().stream().map(record -> {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("id", record.getId());
            recordMap.put("userId", record.getUserId());
            recordMap.put("exerciseType", record.getExerciseType());
            recordMap.put("exerciseName", record.getExerciseName());
            recordMap.put("duration", record.getDuration());
            recordMap.put("calories", record.getCalories());
            recordMap.put("exerciseTime", record.getExerciseTime());
            recordMap.put("note", record.getNote());
            recordMap.put("createdAt", record.getCreatedAt());

            User user = userMap.get(record.getUserId());
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("nickname", user.getName());
                userInfo.put("openid", user.getOpenid());
                recordMap.put("user", userInfo);
            }

            return recordMap;
        }).collect(Collectors.toList());

        Page<Map<String, Object>> pageResult = new Page<>();
        pageResult.setRecords(records);
        pageResult.setTotal(result.getTotal());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());

        return ApiResponse.ok(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取运动记录详情")
    public ApiResponse<Map<String, Object>> getExerciseRecordDetail(@PathVariable("id") Long id) {
        ExerciseRecord record = exerciseRecordService.getById(id);
        if (record == null) {
            return ApiResponse.fail(404, "运动记录不存在");
        }

        // 获取用户信息
        User user = userService.getById(record.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("userId", record.getUserId());
        result.put("exerciseType", record.getExerciseType());
        result.put("exerciseName", record.getExerciseName());
        result.put("duration", record.getDuration());
        result.put("calories", record.getCalories());
        result.put("exerciseTime", record.getExerciseTime());
        result.put("note", record.getNote());
        result.put("createdAt", record.getCreatedAt());

        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("nickname", user.getName());
            userInfo.put("openid", user.getOpenid());
            result.put("user", userInfo);
        }

        return ApiResponse.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新运动记录")
    public ApiResponse<ExerciseRecord> updateExerciseRecord(@PathVariable("id") Long id, @RequestBody ExerciseRecord record) {
        ExerciseRecord existing = exerciseRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "运动记录不存在");
        }

        record.setId(id);
        record.setCreatedAt(existing.getCreatedAt()); // 保持原创建时间
        exerciseRecordService.updateById(record);
        return ApiResponse.ok(record);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除运动记录")
    public ApiResponse<Void> deleteExerciseRecord(@PathVariable("id") Long id) {
        ExerciseRecord existing = exerciseRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "运动记录不存在");
        }

        exerciseRecordService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取运动记录统计")
    public ApiResponse<Map<String, Object>> getExerciseRecordStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总运动记录数
        stats.put("totalExerciseRecords", exerciseRecordService.count());
        
        // 获取今日新增运动记录数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("todayExerciseRecords", exerciseRecordService.lambdaQuery()
                .ge(ExerciseRecord::getCreatedAt, todayStart)
                .le(ExerciseRecord::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.ok(stats);
    }
}