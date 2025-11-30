package com.example.health.web.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.dto.RecipeItemDto;
import com.example.health.domain.entity.Recipe;
import com.example.health.domain.entity.RecipeItem;
import com.example.health.service.RecipeService;
import com.example.health.service.RecipeItemService;
import com.example.health.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/recipes")
@Tag(name = "Admin Recipes")
public class AdminRecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    @Autowired
    private RecipeItemService recipeItemService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @GetMapping
    @Operation(summary = "获取食谱列表")
    public ApiResponse<Page<Recipe>> getRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer enabled
    ) {
        Page<Recipe> pageParam = new Page<>(page, size);
        Page<Recipe> result = recipeService.getList(pageParam, keyword, categoryId, enabled);
        return ApiResponse.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取食谱详情")
    public ApiResponse<Recipe> getRecipe(@PathVariable Long id) {
        Recipe recipe = recipeService.getDetail(id);
        if (recipe == null) {
            return ApiResponse.error("食谱不存在");
        }
        return ApiResponse.success(recipe);
    }
    
    @GetMapping("/{id}/items")
    @Operation(summary = "获取食谱明细")
    public ApiResponse<List<RecipeItemDto>> getRecipeItems(@PathVariable Long id) {
        List<RecipeItemDto> items = recipeItemService.getRecipeItemsWithFoodName(id);
        return ApiResponse.success(items);
    }
    
    @PostMapping
    @Operation(summary = "创建食谱")
    public ApiResponse<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe created = recipeService.createRecipe(recipe, null);
        return ApiResponse.success(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新食谱")
    public ApiResponse<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipe) {
        recipe.setId(id);
        Recipe updated = recipeService.updateRecipe(recipe, null);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除食谱")
    public ApiResponse<Void> deleteRecipe(@PathVariable Long id) {
        boolean success = recipeService.deleteRecipe(id);
        return success ? ApiResponse.success() : ApiResponse.error("删除失败");
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "保存食谱食材")
    public ApiResponse<Void> saveRecipeItems(@PathVariable("id") Long recipeId, 
                                           @RequestBody List<RecipeItem> items) {
        try {
            // 先删除现有食材
            recipeItemService.deleteByRecipeId(recipeId);
            // 保存新食材
            for (RecipeItem item : items) {
                item.setRecipeId(recipeId);
                recipeItemService.save(item);
            }
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("保存食材失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/items")
    @Operation(summary = "更新食谱食材")
    public ApiResponse<Void> updateRecipeItems(@PathVariable("id") Long recipeId, 
                                             @RequestBody List<RecipeItem> items) {
        try {
            // 先删除现有食材
            recipeItemService.deleteByRecipeId(recipeId);
            // 保存新食材
            for (RecipeItem item : items) {
                item.setRecipeId(recipeId);
                recipeItemService.save(item);
            }
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("更新食材失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{recipeId}/items/{itemId}")
    @Operation(summary = "删除食谱食材")
    public ApiResponse<Void> deleteRecipeItem(@PathVariable("recipeId") Long recipeId, 
                                             @PathVariable("itemId") Long itemId) {
        try {
            boolean success = recipeItemService.removeById(itemId);
            return success ? ApiResponse.success() : ApiResponse.error("删除失败");
        } catch (Exception e) {
            return ApiResponse.error("删除食材失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/upload-image")
    @Operation(summary = "上传食谱图片")
    public ApiResponse<Map<String, String>> uploadRecipeImage(@RequestParam("image") MultipartFile file) {
        try {
            String imageUrl = fileUploadService.uploadRecipeImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("imageUrl", imageUrl);
            result.put("message", "图片上传成功");
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "获取食谱统计")
    public ApiResponse<Map<String, Object>> getRecipeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取总食谱数（只统计启用的）
        stats.put("totalRecipes", recipeService.lambdaQuery().eq(Recipe::getEnabled, 1).count());
        
        // 获取今日新增食谱数
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        stats.put("todayRecipes", recipeService.lambdaQuery()
                .eq(Recipe::getEnabled, 1)
                .ge(Recipe::getCreatedAt, todayStart)
                .le(Recipe::getCreatedAt, todayEnd)
                .count());
        
        return ApiResponse.success(stats);
    }
}
