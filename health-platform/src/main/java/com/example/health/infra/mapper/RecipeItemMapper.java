package com.example.health.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.health.domain.dto.RecipeItemDto;
import com.example.health.domain.entity.RecipeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecipeItemMapper extends BaseMapper<RecipeItem> {

    /**
     * 获取食谱明细（包含食物名称）
     */
    @Select("SELECT ri.id, ri.recipe_id, ri.food_id, f.name as food_name, " +
            "ri.quantity, ri.quantity_unit, ri.calories, ri.created_at " +
            "FROM recipe_items ri " +
            "LEFT JOIN foods f ON ri.food_id = f.id " +
            "WHERE ri.recipe_id = #{recipeId} " +
            "ORDER BY ri.id")
    List<RecipeItemDto> getRecipeItemsWithFoodName(Long recipeId);
}

