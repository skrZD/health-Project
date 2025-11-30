package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("diet_record_items")
public class DietRecordItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("record_id")
    private Long dietRecordId;
    
    @TableField("food_id")
    private Long foodId;
    
    @TableField("recipe_id")
    private Long recipeId;
    
    // food_name 字段在数据库中不存在，需要通过关联查询获取
    @TableField(exist = false)
    private String foodName;
    
    @TableField("quantity")
    private BigDecimal quantity;
    
    @TableField("quantity_unit")
    private String unit;
    
    @TableField("calories")
    private Integer calories;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
