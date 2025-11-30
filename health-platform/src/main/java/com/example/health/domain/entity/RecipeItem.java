package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recipe_items")
public class RecipeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recipeId;
    private Long foodId;
    private BigDecimal quantity;
    private String quantityUnit;
    private BigDecimal calories;
    private LocalDateTime createdAt;
}

