package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.RecipeCategory;
import com.example.health.infra.mapper.RecipeCategoryMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecipeCategoryService extends ServiceImpl<RecipeCategoryMapper, RecipeCategory> {
    
    /**
     * 获取所有分类（树形结构）
     */
    public List<RecipeCategory> getTree() {
        QueryWrapper<RecipeCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "created_at");
        return this.list(queryWrapper);
    }
    
    /**
     * 获取顶级分类
     */
    public List<RecipeCategory> getTopLevel() {
        QueryWrapper<RecipeCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("parent_id");
        queryWrapper.orderByAsc("sort", "created_at");
        return this.list(queryWrapper);
    }
    
    /**
     * 根据父级ID获取子分类
     */
    public List<RecipeCategory> getByParentId(Long parentId) {
        QueryWrapper<RecipeCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.orderByAsc("sort", "created_at");
        return this.list(queryWrapper);
    }
    
    /**
     * 创建分类
     */
    public RecipeCategory createCategory(RecipeCategory category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        this.save(category);
        return category;
    }
    
    /**
     * 更新分类
     */
    public RecipeCategory updateCategory(RecipeCategory category) {
        category.setUpdatedAt(LocalDateTime.now());
        this.updateById(category);
        return category;
    }
    
    /**
     * 删除分类（检查是否有子分类）
     */
    public boolean deleteCategory(Long id) {
        // 检查是否有子分类
        QueryWrapper<RecipeCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        long childCount = this.count(queryWrapper);
        
        if (childCount > 0) {
            throw new RuntimeException("该分类下还有子分类，无法删除");
        }
        
        return this.removeById(id);
    }
}
