package com.example.health.web;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.FoodCategory;
import com.example.health.service.FoodCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food-categories")
@Tag(name = "Food Categories")
public class FoodCategoryController {
    private final FoodCategoryService foodCategoryService;

    public FoodCategoryController(FoodCategoryService foodCategoryService) {
        this.foodCategoryService = foodCategoryService;
    }

    @GetMapping
    @Operation(summary = "查询食物分类列表")
    public ApiResponse<List<FoodCategory>> getFoodCategories() {
        List<FoodCategory> categories = foodCategoryService.list();
        return ApiResponse.ok(categories);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询食物分类详情")
    public ApiResponse<FoodCategory> getFoodCategory(@PathVariable("id") Long id) {
        FoodCategory category = foodCategoryService.getById(id);
        if (category == null) {
            return ApiResponse.fail(404, "食物分类不存在");
        }
        return ApiResponse.ok(category);
    }
}
