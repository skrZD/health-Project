package com.example.health.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.dto.RecipeDetailDto;
import com.example.health.domain.dto.RecipeItemDto;
import com.example.health.domain.entity.Recipe;
import com.example.health.domain.entity.RecipeItem;
import com.example.health.service.RecipeService;
import com.example.health.service.RecipeItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final RecipeItemService recipeItemService;

    public RecipeController(RecipeService recipeService, RecipeItemService recipeItemService) {
        this.recipeService = recipeService;
        this.recipeItemService = recipeItemService;
    }

    @GetMapping
    @Operation(summary = "查询食谱列表")
    public ApiResponse<Page<Recipe>> getRecipes(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "enabled", required = false) Integer enabled,
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        Page<Recipe> pageParam = new Page<>(page, size);
        Page<Recipe> result = recipeService.getList(pageParam, keyword, categoryId, enabled);
        return ApiResponse.success(result);
    }

    @GetMapping("/enabled")
    @Operation(summary = "查询启用的食谱列表")
    public ApiResponse<List<Recipe>> getEnabledRecipes() {
        List<Recipe> recipes = recipeService.getEnabledList();
        return ApiResponse.success(recipes);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询食谱")
    public ApiResponse<List<Recipe>> getRecipesByCategory(@PathVariable("categoryId") Long categoryId) {
        List<Recipe> recipes = recipeService.getByCategory(categoryId);
        return ApiResponse.success(recipes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询食谱详情")
    public ApiResponse<Recipe> getRecipe(@PathVariable("id") Long id) {
        Recipe recipe = recipeService.getDetail(id);
        if (recipe == null) {
            return ApiResponse.error("食谱不存在");
        }
        return ApiResponse.success(recipe);
    }
    
    @GetMapping("/{id}/detail")
    @Operation(summary = "查询食谱完整详情（包含食材明细）")
    public ApiResponse<RecipeDetailDto> getRecipeDetail(@PathVariable("id") Long id) {
        RecipeDetailDto recipeDetail = recipeService.getDetailWithItems(id);
        if (recipeDetail == null) {
            return ApiResponse.error("食谱不存在");
        }
        return ApiResponse.success(recipeDetail);
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "查询食谱明细")
    public ApiResponse<List<RecipeItemDto>> getRecipeItems(@PathVariable("id") Long id) {
        List<RecipeItemDto> items = recipeItemService.getRecipeItemsWithFoodName(id);
        return ApiResponse.success(items);
    }

    @PostMapping
    @Operation(summary = "创建食谱")
    public ApiResponse<Recipe> createRecipe(@RequestBody Recipe recipe, 
                                           @RequestParam(required = false) List<RecipeItem> items) {
        Recipe created = recipeService.createRecipe(recipe, items);
        return ApiResponse.success(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新食谱")
    public ApiResponse<Recipe> updateRecipe(@PathVariable("id") Long id, 
                                           @RequestBody Recipe recipe,
                                           @RequestParam(required = false) List<RecipeItem> items) {
        recipe.setId(id);
        Recipe updated = recipeService.updateRecipe(recipe, items);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除食谱")
    public ApiResponse<Void> deleteRecipe(@PathVariable("id") Long id) {
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
}
