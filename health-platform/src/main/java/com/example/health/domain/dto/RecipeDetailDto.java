package com.example.health.domain.dto;

import com.example.health.domain.entity.Recipe;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDetailDto {
    private Recipe recipe;
    private List<RecipeItemDto> items;
    
    public RecipeDetailDto(Recipe recipe, List<RecipeItemDto> items) {
        this.recipe = recipe;
        this.items = items;
    }
}


