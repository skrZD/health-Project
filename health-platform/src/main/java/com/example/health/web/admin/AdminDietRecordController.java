package com.example.health.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.DietRecord;
import com.example.health.domain.entity.DietRecordItem;
import com.example.health.domain.entity.User;
import com.example.health.service.DietRecordService;
import com.example.health.service.DietRecordItemService;
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
@RequestMapping("/api/admin/diet-records")
@Tag(name = "Admin Diet Records")
public class AdminDietRecordController {
    private final DietRecordService dietRecordService;
    private final DietRecordItemService dietRecordItemService;
    private final UserService userService;

    public AdminDietRecordController(DietRecordService dietRecordService, 
                                   DietRecordItemService dietRecordItemService,
                                   UserService userService) {
        this.dietRecordService = dietRecordService;
        this.dietRecordItemService = dietRecordItemService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "获取饮食记录列表")
    public ApiResponse<Page<Map<String, Object>>> getDietRecordList(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "mealType", required = false) String mealType,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ) {
        LambdaQueryWrapper<DietRecord> qw = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(DietRecord::getNote, keyword);
        }
        if (userId != null) {
            qw.eq(DietRecord::getUserId, userId);
        }
        if (mealType != null && !mealType.isEmpty()) {
            qw.eq(DietRecord::getMealType, mealType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(DietRecord::getRecordedAt, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(DietRecord::getRecordedAt, endDate);
        }
        
        qw.orderByDesc(DietRecord::getRecordedAt);
        
        Page<DietRecord> result = dietRecordService.page(Page.of(page, size), qw);
        
        // 获取用户信息并构建返回数据
        List<Long> userIds = result.getRecords().stream()
                .map(DietRecord::getUserId)
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
            recordMap.put("mealType", record.getMealType());
            recordMap.put("recordedAt", record.getRecordedAt());
            recordMap.put("planCalories", record.getPlanCalories());
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
    @Operation(summary = "获取饮食记录详情")
    public ApiResponse<Map<String, Object>> getDietRecordDetail(@PathVariable("id") Long id) {
        DietRecord record = dietRecordService.getById(id);
        if (record == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        
        // 获取用户信息
        User user = userService.getById(record.getUserId());
        
        // 获取饮食记录项
        List<DietRecordItem> items = dietRecordItemService.lambdaQuery()
                .eq(DietRecordItem::getDietRecordId, id)
                .list();
        
        // 为每个饮食记录项设置食物名称
        for (DietRecordItem item : items) {
            if (item.getFoodId() != null) {
                // 这里可以通过foodService获取食物名称
                // 暂时设置为空，后续可以通过关联查询优化
                item.setFoodName("食物ID: " + item.getFoodId());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", record.getId());
        result.put("userId", record.getUserId());
        result.put("mealType", record.getMealType());
        result.put("recordedAt", record.getRecordedAt());
        result.put("planCalories", record.getPlanCalories());
        result.put("note", record.getNote());
        result.put("createdAt", record.getCreatedAt());
        
        if (user != null) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("nickname", user.getName());
            userInfo.put("openid", user.getOpenid());
            result.put("user", userInfo);
        }
        
        result.put("items", items);
        
        return ApiResponse.ok(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新饮食记录")
    public ApiResponse<DietRecord> updateDietRecord(@PathVariable("id") Long id, @RequestBody DietRecord record) {
        DietRecord existing = dietRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        
        record.setId(id);
        record.setCreatedAt(existing.getCreatedAt()); // 保持原创建时间
        dietRecordService.updateById(record);
        return ApiResponse.ok(record);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除饮食记录")
    public ApiResponse<Void> deleteDietRecord(@PathVariable("id") Long id) {
        DietRecord existing = dietRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        
        // 删除关联的饮食记录项
        LambdaQueryWrapper<DietRecordItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(DietRecordItem::getDietRecordId, id);
        dietRecordItemService.remove(itemWrapper);
        
        // 删除饮食记录
        dietRecordService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/stats")
    @Operation(summary = "获取饮食记录统计")
    public ApiResponse<Map<String, Object>> getDietRecordStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总饮食记录数
        stats.put("totalDietRecords", dietRecordService.count());
        
        // 获取今日新增饮食记录数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("todayDietRecords", dietRecordService.lambdaQuery()
                .ge(DietRecord::getCreatedAt, todayStart)
                .le(DietRecord::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.ok(stats);
    }
}
