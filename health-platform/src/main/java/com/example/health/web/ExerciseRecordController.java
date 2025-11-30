package com.example.health.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.common.util.SecurityUtils;
import com.example.health.domain.entity.ExerciseRecord;
import com.example.health.service.ExerciseRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercise-records")
@Tag(name = "Exercise Records")
public class ExerciseRecordController {
    private final ExerciseRecordService exerciseRecordService;

    public ExerciseRecordController(ExerciseRecordService exerciseRecordService) {
        this.exerciseRecordService = exerciseRecordService;
    }

    @PostMapping
    @Operation(summary = "创建运动记录")
    public ApiResponse<ExerciseRecord> createExerciseRecord(@RequestBody ExerciseRecord exerciseRecord) {
        // 添加调试日志
        System.out.println("接收到的运动记录数据: " + exerciseRecord);
        System.out.println("exerciseType: " + exerciseRecord.getExerciseType());
        System.out.println("exerciseName: " + exerciseRecord.getExerciseName());
        System.out.println("duration: " + exerciseRecord.getDuration());
        System.out.println("calories: " + exerciseRecord.getCalories());
        System.out.println("exerciseTime: " + exerciseRecord.getExerciseTime());
        System.out.println("note: " + exerciseRecord.getNote());
        
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        exerciseRecord.setUserId(currentUserId);
        exerciseRecord.setCreatedAt(LocalDateTime.now());
        exerciseRecord.setUpdatedAt(LocalDateTime.now());
        exerciseRecordService.save(exerciseRecord);
        return ApiResponse.ok(exerciseRecord);
    }

    @GetMapping
    @Operation(summary = "查询运动记录列表")
    public ApiResponse<Page<ExerciseRecord>> getExerciseRecords(
            @RequestParam(value = "exerciseType", required = false) String exerciseType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        LambdaQueryWrapper<ExerciseRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(ExerciseRecord::getUserId, currentUserId);
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
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询运动记录详情")
    public ApiResponse<ExerciseRecord> getExerciseRecord(@PathVariable("id") Long id) {
        ExerciseRecord exerciseRecord = exerciseRecordService.getById(id);
        if (exerciseRecord == null) {
            return ApiResponse.fail(404, "运动记录不存在");
        }
        return ApiResponse.ok(exerciseRecord);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新运动记录")
    public ApiResponse<ExerciseRecord> updateExerciseRecord(@PathVariable("id") Long id, @RequestBody ExerciseRecord exerciseRecord) {
        ExerciseRecord existing = exerciseRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "运动记录不存在");
        }
        exerciseRecord.setId(id);
        exerciseRecord.setUpdatedAt(LocalDateTime.now());
        exerciseRecordService.updateById(exerciseRecord);
        return ApiResponse.ok(exerciseRecord);
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
    @Operation(summary = "查询运动统计")
    public ApiResponse<Map<String, Object>> getExerciseStats(
            @RequestParam(value = "date", required = false) String date
    ) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        LambdaQueryWrapper<ExerciseRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(ExerciseRecord::getUserId, currentUserId);
        
        if (date != null && !date.isEmpty()) {
            LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");
            qw.between(ExerciseRecord::getExerciseTime, startOfDay, endOfDay);
        }
        
        List<ExerciseRecord> records = exerciseRecordService.list(qw);
        
        int totalDuration = records.stream().mapToInt(ExerciseRecord::getDuration).sum();
        int totalCalories = records.stream().mapToInt(ExerciseRecord::getCalories).sum();
        int totalCount = records.size();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDuration", totalDuration);
        stats.put("totalCalories", totalCalories);
        stats.put("totalCount", totalCount);
        
        return ApiResponse.ok(stats);
    }
}
