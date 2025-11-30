package com.example.health.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.Food;
import com.example.health.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "Foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/search")
    @Operation(summary = "食物搜索（分页）")
    public ApiResponse<Page<Food>> search(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "categoryId", required = false) String categoryIdStr,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        LambdaQueryWrapper<Food> qw = new LambdaQueryWrapper<>();
        if (q != null && !q.isEmpty()) {
            qw.like(Food::getName, q).or().like(Food::getAlias, q);
        }
        
        // 处理categoryId参数，支持null字符串
        Long categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.isEmpty() && !"null".equals(categoryIdStr)) {
            try {
                categoryId = Long.parseLong(categoryIdStr);
            } catch (NumberFormatException e) {
                // 如果转换失败，忽略该参数
            }
        }
        
        if (categoryId != null) {
            qw.eq(Food::getCategoryId, categoryId);
        }
        qw.eq(Food::getEnabled, 1);
        Page<Food> p = foodService.page(Page.of(page, size), qw);
        return ApiResponse.ok(p);
    }
}



