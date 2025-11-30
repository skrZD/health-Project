package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("foods")
public class Food {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("category_id")
    private Long categoryId;
    
    private String name;
    private String alias;
    private String unit;
    
    @TableField("calories_per_100")
    private Double caloriesPer100;
    
    @TableField("protein_g_per_100")
    private Double proteinGPer100;
    
    @TableField("fat_g_per_100")
    private Double fatGPer100;
    
    @TableField("carbs_g_per_100")
    private Double carbsGPer100;
    
    @TableField("sodium_mg_per_100")
    private Double sodiumMgPer100;
    
    private Integer enabled;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}



