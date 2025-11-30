package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("diet_records")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DietRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("meal_type")
    private String mealType; // 早餐/午餐/晚饭/加餐
    
    @TableField("recorded_at")
    private LocalDateTime recordedAt;
    
    @TableField("plan_calories")
    private Integer planCalories;
    
    private String note;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    // 饮食记录明细（不存储在数据库中，仅用于数据传输）
    @TableField(exist = false)
    private List<DietRecordItem> items;
}
