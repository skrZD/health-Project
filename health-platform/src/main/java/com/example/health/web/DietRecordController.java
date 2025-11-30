package com.example.health.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.common.util.SecurityUtils;
import com.example.health.domain.entity.DietRecord;
import com.example.health.domain.entity.DietRecordItem;
import com.example.health.service.DietRecordService;
import com.example.health.service.DietRecordItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diet-records")
@Tag(name = "Diet Records")
public class DietRecordController {
    private final DietRecordService dietRecordService;
    private final DietRecordItemService dietRecordItemService;

    public DietRecordController(DietRecordService dietRecordService, DietRecordItemService dietRecordItemService) {
        this.dietRecordService = dietRecordService;
        this.dietRecordItemService = dietRecordItemService;
    }

    @PostMapping
    @Operation(summary = "创建饮食记录")
    public ApiResponse<DietRecord> createDietRecord(@RequestBody DietRecord dietRecord) {
        // 获取当前登录用户ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ApiResponse.fail(401, "用户未登录");
        }
        
        dietRecord.setUserId(currentUserId);
        dietRecord.setCreatedAt(LocalDateTime.now());
        
        // 保存饮食记录
        dietRecordService.save(dietRecord);
        
        // 保存饮食记录明细
        if (dietRecord.getItems() != null && !dietRecord.getItems().isEmpty()) {
            for (DietRecordItem item : dietRecord.getItems()) {
                item.setDietRecordId(dietRecord.getId());
                item.setCreatedAt(LocalDateTime.now());
            }
            dietRecordItemService.saveBatch(dietRecord.getItems());
        }
        
        return ApiResponse.ok(dietRecord);
    }

    @GetMapping
    @Operation(summary = "查询饮食记录列表")
    public ApiResponse<Page<DietRecord>> getDietRecords(
            @RequestParam(value = "mealType", required = false) String mealType,
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
        
        LambdaQueryWrapper<DietRecord> qw = new LambdaQueryWrapper<>();
        qw.eq(DietRecord::getUserId, currentUserId);
        if (mealType != null && !mealType.isEmpty()) {
            qw.eq(DietRecord::getMealType, mealType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            qw.ge(DietRecord::getRecordedAt, LocalDateTime.parse(startDate + "T00:00:00"));
        }
        if (endDate != null && !endDate.isEmpty()) {
            qw.le(DietRecord::getRecordedAt, LocalDateTime.parse(endDate + "T23:59:59"));
        }
        qw.orderByDesc(DietRecord::getRecordedAt);
        
        Page<DietRecord> result = dietRecordService.page(Page.of(page, size), qw);
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询饮食记录详情")
    public ApiResponse<DietRecord> getDietRecord(@PathVariable("id") Long id) {
        DietRecord dietRecord = dietRecordService.getById(id);
        if (dietRecord == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        return ApiResponse.ok(dietRecord);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新饮食记录")
    public ApiResponse<DietRecord> updateDietRecord(@PathVariable("id") Long id, @RequestBody DietRecord dietRecord) {
        DietRecord existing = dietRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        dietRecord.setId(id);
        dietRecordService.updateById(dietRecord);
        return ApiResponse.ok(dietRecord);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除饮食记录")
    public ApiResponse<Void> deleteDietRecord(@PathVariable("id") Long id) {
        DietRecord existing = dietRecordService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "饮食记录不存在");
        }
        dietRecordService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询饮食记录明细")
    public ApiResponse<List<DietRecordItem>> getDietRecordItems(@PathVariable("id") Long id) {
        LambdaQueryWrapper<DietRecordItem> qw = new LambdaQueryWrapper<>();
        qw.eq(DietRecordItem::getDietRecordId, id);
        List<DietRecordItem> items = dietRecordItemService.list(qw);
        return ApiResponse.ok(items);
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "添加饮食记录明细")
    public ApiResponse<DietRecordItem> addDietRecordItem(@PathVariable("id") Long id, @RequestBody DietRecordItem item) {
        item.setDietRecordId(id);
        item.setCreatedAt(LocalDateTime.now());
        dietRecordItemService.save(item);
        return ApiResponse.ok(item);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "删除饮食记录明细")
    public ApiResponse<Void> deleteDietRecordItem(@PathVariable("itemId") Long itemId) {
        dietRecordItemService.removeById(itemId);
        return ApiResponse.ok();
    }
}
