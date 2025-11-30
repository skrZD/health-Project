package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.dto.RecipeDetailDto;
import com.example.health.domain.dto.RecipeItemDto;
import com.example.health.domain.entity.Recipe;
import com.example.health.domain.entity.RecipeItem;
import com.example.health.infra.mapper.RecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeService extends ServiceImpl<RecipeMapper, Recipe> {
    
    @Autowired
    private RecipeItemService recipeItemService;
    
    /**
     * 获取食谱列表（分页）
     */
    public Page<Recipe> getList(Page<Recipe> page, String keyword, Long categoryId, Integer enabled) {
        QueryWrapper<Recipe> queryWrapper = new QueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("name", keyword).or().like("description", keyword);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (enabled != null) {
            queryWrapper.eq("enabled", enabled);
        }
        
        queryWrapper.orderByDesc("created_at");
        return this.page(page, queryWrapper);
    }
    
    /**
     * 获取启用的食谱列表
     */
    public List<Recipe> getEnabledList() {
        QueryWrapper<Recipe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", 1);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }
    
    /**
     * 根据分类获取食谱
     */
    public List<Recipe> getByCategory(Long categoryId) {
        QueryWrapper<Recipe> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.eq("enabled", 1);
        queryWrapper.orderByDesc("created_at");
        return this.list(queryWrapper);
    }
    
    /**
     * 创建食谱（包含明细）
     */
    public Recipe createRecipe(Recipe recipe, List<RecipeItem> items) {
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());
        this.save(recipe);
        
        // 保存食谱明细
        if (items != null && !items.isEmpty()) {
            for (RecipeItem item : items) {
                item.setRecipeId(recipe.getId());
                item.setCreatedAt(LocalDateTime.now());
            }
            recipeItemService.saveBatch(items);
        }
        
        return recipe;
    }
    
    /**
     * 更新食谱（包含明细）
     */
    public Recipe updateRecipe(Recipe recipe, List<RecipeItem> items) {
        recipe.setUpdatedAt(LocalDateTime.now());
        this.updateById(recipe);
        
        // 只有当items不为null时才更新明细
        if (items != null) {
            // 删除原有明细
            QueryWrapper<RecipeItem> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("recipe_id", recipe.getId());
            recipeItemService.remove(deleteWrapper);
            
            // 保存新明细
            if (!items.isEmpty()) {
                for (RecipeItem item : items) {
                    item.setRecipeId(recipe.getId());
                    item.setCreatedAt(LocalDateTime.now());
                }
                recipeItemService.saveBatch(items);
            }
        }
        
        return recipe;
    }
    
    /**
     * 获取食谱详情（包含明细）
     */
    public Recipe getDetail(Long id) {
        Recipe recipe = this.getById(id);
        if (recipe != null) {
            QueryWrapper<RecipeItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("recipe_id", id);
            List<RecipeItem> items = recipeItemService.list(queryWrapper);
            // 这里可以设置到recipe对象中，如果需要的话
        }
        return recipe;
    }
    
    /**
     * 获取食谱完整详情（包含食材明细）
     */
    public RecipeDetailDto getDetailWithItems(Long id) {
        Recipe recipe = this.getById(id);
        if (recipe == null) {
            return null;
        }
        
        List<RecipeItemDto> items = recipeItemService.getRecipeItemsWithFoodName(id);
        return new RecipeDetailDto(recipe, items);
    }
    
    /**
     * 删除食谱（包含明细）
     */
    public boolean deleteRecipe(Long id) {
        // 删除明细
        QueryWrapper<RecipeItem> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("recipe_id", id);
        recipeItemService.remove(deleteWrapper);
        
        // 删除食谱
        return this.removeById(id);
    }
}

