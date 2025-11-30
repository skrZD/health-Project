package com.example.health.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecipeItemDto {
    private Long id;
    private Long recipeId;
    private Long foodId;
    private String foodName;  // 食物名称
    private BigDecimal quantity;
    private String quantityUnit;
    private BigDecimal calories;  // 热量
    private LocalDateTime createdAt;
}


