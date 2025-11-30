package com.example.health.web.admin;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.FoodCategory;
import com.example.health.service.FoodCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/food-categories")
@Tag(name = "Admin Food Categories")
public class AdminFoodCategoryController {
    private final FoodCategoryService foodCategoryService;

    public AdminFoodCategoryController(FoodCategoryService foodCategoryService) {
        this.foodCategoryService = foodCategoryService;
    }

    @GetMapping
    @Operation(summary = "获取食物分类列表")
    public ApiResponse<List<FoodCategory>> getFoodCategories() {
        List<FoodCategory> categories = foodCategoryService.list();
        return ApiResponse.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取食物分类详情")
    public ApiResponse<FoodCategory> getFoodCategory(@PathVariable("id") Long id) {
        FoodCategory category = foodCategoryService.getById(id);
        if (category == null) {
            return ApiResponse.fail(404, "分类不存在");
        }
        return ApiResponse.ok(category);
    }

    @PostMapping
    @Operation(summary = "创建食物分类")
    public ApiResponse<FoodCategory> createFoodCategory(@RequestBody FoodCategory category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        foodCategoryService.save(category);
        return ApiResponse.ok(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新食物分类")
    public ApiResponse<FoodCategory> updateFoodCategory(@PathVariable("id") Long id, @RequestBody FoodCategory category) {
        FoodCategory existing = foodCategoryService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "分类不存在");
        }
        
        category.setId(id);
        category.setUpdatedAt(LocalDateTime.now());
        foodCategoryService.updateById(category);
        return ApiResponse.ok(category);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除食物分类")
    public ApiResponse<Void> deleteFoodCategory(@PathVariable("id") Long id) {
        FoodCategory existing = foodCategoryService.getById(id);
        if (existing == null) {
            return ApiResponse.fail(404, "分类不存在");
        }
        
        foodCategoryService.removeById(id);
        return ApiResponse.ok();
    }
}

