package com.example.health.web;

import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.RecipeCategory;
import com.example.health.service.RecipeCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-categories")
@Tag(name = "Recipe Categories")
public class RecipeCategoryController {
    
    @Autowired
    private RecipeCategoryService recipeCategoryService;
    
    @GetMapping
    @Operation(summary = "获取食谱分类列表")
    public ApiResponse<List<RecipeCategory>> getCategories() {
        List<RecipeCategory> categories = recipeCategoryService.getTree();
        return ApiResponse.success(categories);
    }
    
    @GetMapping("/top-level")
    @Operation(summary = "获取顶级分类")
    public ApiResponse<List<RecipeCategory>> getTopLevelCategories() {
        List<RecipeCategory> categories = recipeCategoryService.getTopLevel();
        return ApiResponse.success(categories);
    }
    
    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子分类")
    public ApiResponse<List<RecipeCategory>> getChildren(@PathVariable Long parentId) {
        List<RecipeCategory> categories = recipeCategoryService.getByParentId(parentId);
        return ApiResponse.success(categories);
    }
    
    @PostMapping
    @Operation(summary = "创建分类")
    public ApiResponse<RecipeCategory> createCategory(@RequestBody RecipeCategory category) {
        RecipeCategory created = recipeCategoryService.createCategory(category);
        return ApiResponse.success(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新分类")
    public ApiResponse<RecipeCategory> updateCategory(@PathVariable Long id, @RequestBody RecipeCategory category) {
        category.setId(id);
        RecipeCategory updated = recipeCategoryService.updateCategory(category);
        return ApiResponse.success(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        try {
            boolean success = recipeCategoryService.deleteCategory(id);
            return success ? ApiResponse.success() : ApiResponse.error("删除失败");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
