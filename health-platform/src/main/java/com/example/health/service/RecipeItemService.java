package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.dto.RecipeItemDto;
import com.example.health.domain.entity.RecipeItem;
import com.example.health.infra.mapper.RecipeItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeItemService extends ServiceImpl<RecipeItemMapper, RecipeItem> {

    @Autowired
    private RecipeItemMapper recipeItemMapper;

    /**
     * 获取食谱明细（包含食物名称）
     */
    public List<RecipeItemDto> getRecipeItemsWithFoodName(Long recipeId) {
        return recipeItemMapper.getRecipeItemsWithFoodName(recipeId);
    }

    /**
     * 根据食谱ID删除所有食材
     */
    public boolean deleteByRecipeId(Long recipeId) {
        LambdaQueryWrapper<RecipeItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RecipeItem::getRecipeId, recipeId);
        return remove(queryWrapper);
    }
}

