package com.example.health.web.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.Food;
import com.example.health.domain.entity.FoodCategory;
import com.example.health.service.FoodService;
import com.example.health.service.FoodCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/foods")
@Tag(name = "Admin Foods")
public class AdminFoodController {
    private final FoodService foodService;
    private final FoodCategoryService foodCategoryService;

    public AdminFoodController(FoodService foodService, FoodCategoryService foodCategoryService) {
        this.foodService = foodService;
        this.foodCategoryService = foodCategoryService;
    }

    @GetMapping
    @Operation(summary = "获取食物列表")
    public ApiResponse<Page<Food>> getFoodList(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "enabled", required = false) Integer enabled
    ) {
        LambdaQueryWrapper<Food> qw = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(Food::getName, keyword).or().like(Food::getAlias, keyword);
        }
        if (categoryId != null) {
            qw.eq(Food::getCategoryId, categoryId);
        }
        if (enabled != null) {
            qw.eq(Food::getEnabled, enabled);
        }
        
        qw.orderByDesc(Food::getCreatedAt);
        
        Page<Food> result = foodService.page(Page.of(page, size), qw);
        return ApiResponse.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取食物详情")
    public ApiResponse<Food> getFoodDetail(@PathVariable("id") Long id) {
        Food food = foodService.getById(id);
        if (food == null) {
            return ApiResponse.fail(404, "食物不存在");
        }
        return ApiResponse.ok(food);
    }

    @PostMapping
    @Operation(summary = "创建食物")
    public ApiResponse<Food> createFood(@RequestBody Food food) {
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(LocalDateTime.now());
        foodService.save(food);
        return ApiResponse.ok(food);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新食物")
    public ApiResponse<Food> updateFood(@PathVariable("id") Long id, @RequestBody Food food) {
        Food existing = foodService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "食物不存在");
        }
        
        food.setId(id);
        food.setUpdatedAt(LocalDateTime.now());
        foodService.updateById(food);
        return ApiResponse.ok(food);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除食物")
    public ApiResponse<Void> deleteFood(@PathVariable("id") Long id) {
        Food existing = foodService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "食物不存在");
        }
        
        foodService.removeById(id);
        return ApiResponse.ok();
    }

    @GetMapping("/categories")
    @Operation(summary = "获取食物分类列表")
    public ApiResponse<List<FoodCategory>> getFoodCategories() {
        List<FoodCategory> categories = foodCategoryService.list();
        return ApiResponse.ok(categories);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取食物统计")
    public ApiResponse<Map<String, Object>> getFoodStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总食物数（只统计启用的）
        stats.put("totalFoods", foodService.lambdaQuery().eq(Food::getEnabled, 1).count());
        
        // 获取今日新增食物数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("todayFoods", foodService.lambdaQuery()
                .eq(Food::getEnabled, 1)
                .ge(Food::getCreatedAt, todayStart)
                .le(Food::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.ok(stats);
    }
}

