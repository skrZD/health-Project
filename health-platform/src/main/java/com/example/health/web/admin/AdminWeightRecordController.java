package com.example.health.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.User;
import com.example.health.domain.entity.WeightRecord;
import com.example.health.service.UserService;
import com.example.health.service.WeightRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/weight-records")
@Tag(name = "Admin Weight Record Management")
public class AdminWeightRecordController {
    private final WeightRecordService weightRecordService;
    private final UserService userService;

    public AdminWeightRecordController(WeightRecordService weightRecordService, UserService userService) {
        this.weightRecordService = weightRecordService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取体重记录列表（支持搜索、分页、筛选）")
    public ApiResponse<Page<Map<String, Object>>> getWeightRecordList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        LambdaQueryWrapper<WeightRecord> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(WeightRecord::getNote, keyword);
        }
        if (userId != null) {
            qw.eq(WeightRecord::getUserId, userId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(WeightRecord::getRecordedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(WeightRecord::getRecordedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        qw.orderByDesc(WeightRecord::getRecordedAt);

        Page<WeightRecord> result = weightRecordService.page(Page.of(page, size), qw);

        // 获取用户信息并构建返回数据
        List<Long> userIds = result.getRecords().stream()
                .map(WeightRecord::getUserId)
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
            recordMap.put("weight", record.getWeight());
            recordMap.put("recordedAt", record.getRecordedAt());
            recordMap.put("note", record.getNote());
            recordMap.put("createdAt", record.getCreatedAt());

            User user = userMap.get(record.getUserId());
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("nickname", user.getName());
                userInfo.put("openid", user.getOpenid());
                recordMap.put("user", userInfo);

                // 计算BMI（需要用户身高信息）
                if (user.getHeight() != null && record.getWeight() != null) {
                    double heightInMeters = user.getHeight().doubleValue() / 100.0;
                    double weightInKg = record.getWeight().doubleValue();
                    double bmi = weightInKg / (heightInMeters * heightInMeters);
                    recordMap.put("bmi", BigDecimal.valueOf(bmi));
                } else {
                    recordMap.put("bmi", null);
                }
            } else {
                recordMap.put("bmi", null);
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
    @Operation(summary = "获取体重记录详情")
    public ApiResponse<Map<String, Object>> getWeightRecordDetail(@PathVariable("id") Long id) {
        WeightRecord record = weightRecordService.getById(id);
        if (record == null) {
            return ApiResponse.fail(404, "体重记录不存在");
        }

        // 获取用户信息
        User user = userService.getById(record.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("userId", record.getUserId());
        result.put("weight", record.getWeight());
        result.put("recordedAt", record.getRecordedAt());
        result.put("note", record.getNote());
        result.put("createdAt", record.getCreatedAt());

        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("nickname", user.getName());
            userInfo.put("openid", user.getOpenid());
            result.put("user", userInfo);

            // 计算BMI（需要用户身高信息）
            if (user.getHeight() != null && record.getWeight() != null) {
                double heightInMeters = user.getHeight().doubleValue() / 100.0;
                double weightInKg = record.getWeight().doubleValue();
                double bmi = weightInKg / (heightInMeters * heightInMeters);
                result.put("bmi", BigDecimal.valueOf(bmi));
            } else {
                result.put("bmi", null);
            }
        } else {
            result.put("bmi", null);
        }

        return ApiResponse.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新体重记录")
    public ApiResponse<WeightRecord> updateWeightRecord(@PathVariable("id") Long id, @RequestBody WeightRecord record) {
        WeightRecord existing = weightRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "体重记录不存在");
        }

        record.setId(id);
        record.setCreatedAt(existing.getCreatedAt()); // 保持原创建时间
        weightRecordService.updateById(record);
        return ApiResponse.ok(record);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除体重记录")
    public ApiResponse<Void> deleteWeightRecord(@PathVariable("id") Long id) {
        WeightRecord existing = weightRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "体重记录不存在");
        }

        weightRecordService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取体重记录统计")
    public ApiResponse<Map<String, Object>> getWeightRecordStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总体重记录数
        stats.put("totalWeightRecords", weightRecordService.count());
        
        // 获取今日新增体重记录数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("todayWeightRecords", weightRecordService.lambdaQuery()
                .ge(WeightRecord::getCreatedAt, todayStart)
                .le(WeightRecord::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.ok(stats);
    }
}
