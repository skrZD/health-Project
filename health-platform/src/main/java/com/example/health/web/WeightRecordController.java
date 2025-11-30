package com.example.health.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.common.util.SecurityUtils;
import com.example.health.domain.entity.WeightRecord;
import com.example.health.service.WeightRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weight-records")
@Tag(name = "Weight Records")
public class WeightRecordController {
    private final WeightRecordService weightRecordService;

    public WeightRecordController(WeightRecordService weightRecordService) {
        this.weightRecordService = weightRecordService;
    }

    @PostMapping
    @Operation(summary = "创建体重记录")
    public ApiResponse<WeightRecord> createWeightRecord(@RequestBody WeightRecord weightRecord) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        weightRecord.setUserId(currentUserId);
        weightRecord.setCreatedAt(LocalDateTime.now());
        weightRecordService.save(weightRecord);
        return ApiResponse.ok(weightRecord);
    }

    @GetMapping
    @Operation(summary = "查询体重记录列表")
    public ApiResponse<Page<WeightRecord>> getWeightRecords(
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
        
        LambdaQueryWrapper<WeightRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(WeightRecord::getUserId, currentUserId);
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(WeightRecord::getRecordedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(WeightRecord::getRecordedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        qw.orderByDesc(WeightRecord::getRecordedAt);
        
        Page<WeightRecord> result = weightRecordService.page(Page.of(page, size), qw);
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询体重记录详情")
    public ApiResponse<WeightRecord> getWeightRecord(@PathVariable("id") Long id) {
        WeightRecord weightRecord = weightRecordService.getById(id);
        if (weightRecord == null) {
            return ApiResponse.fail(404, "体重记录不存在");
        }
        return ApiResponse.ok(weightRecord);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新体重记录")
    public ApiResponse<WeightRecord> updateWeightRecord(@PathVariable("id") Long id, @RequestBody WeightRecord weightRecord) {
        WeightRecord existing = weightRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "体重记录不存在");
        }
        weightRecord.setId(id);
        weightRecordService.updateById(weightRecord);
        return ApiResponse.ok(weightRecord);
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

    @GetMapping("/trend")
    @Operation(summary = "查询体重趋势")
    public ApiResponse<Map<String, Object>> getWeightTrend(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "days", defaultValue = "7") int days
    ) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        LambdaQueryWrapper<WeightRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(WeightRecord::getUserId, userId)
          .between(WeightRecord::getRecordedAt, startDate, endDate)
          .orderByAsc(WeightRecord::getRecordedAt);
        
        List<WeightRecord> records = weightRecordService.list(qw);
        
        Map<String, Object> trend = new HashMap<>();
        trend.put("records", records);
        trend.put("latestWeight", records.isEmpty() ? null : records.get(records.size() - 1).getWeight());
        trend.put("weightChange", calculateWeightChange(records));
        
        return ApiResponse.ok(trend);
    }

    @GetMapping("/bmi")
    @Operation(summary = "计算BMI")
    public ApiResponse<Map<String, Object>> calculateBMI(
            @RequestParam(value = "userId") Long userId,
            @RequestParam(value = "heightCm") BigDecimal heightCm
    ) {
        WeightRecord latestWeight = getLatestWeight(userId);
        if (latestWeight == null) {
            return ApiResponse.fail(404, "未找到体重记录");
        }
        
        BigDecimal heightM = heightCm.divide(new BigDecimal("100"));
        BigDecimal bmi = latestWeight.getWeight().divide(heightM.multiply(heightM), 2, BigDecimal.ROUND_HALF_UP);
        
        Map<String, Object> result = new HashMap<>();
        result.put("weight", latestWeight.getWeight());
        result.put("height", heightCm);
        result.put("bmi", bmi);
        result.put("status", getBMIStatus(bmi));
        
        return ApiResponse.ok(result);
    }

    private WeightRecord getLatestWeight(Long userId) {
        LambdaQueryWrapper<WeightRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(WeightRecord::getUserId, userId)
          .orderByDesc(WeightRecord::getRecordedAt)
          .last("LIMIT 1");
        return weightRecordService.getOne(qw);
    }

    private BigDecimal calculateWeightChange(List<WeightRecord> records) {
        if (records.size() < 2) {
            return BigDecimal.ZERO;
        }
        WeightRecord latest = records.get(records.size() - 1);
        WeightRecord previous = records.get(records.size() - 2);
        return latest.getWeight().subtract(previous.getWeight());
    }

    private String getBMIStatus(BigDecimal bmi) {
        double bmiValue = bmi.doubleValue();
        if (bmiValue < 18.5) return "偏瘦";
        if (bmiValue < 24) return "正常";
        if (bmiValue < 28) return "偏胖";
        return "肥胖";
    }
}
