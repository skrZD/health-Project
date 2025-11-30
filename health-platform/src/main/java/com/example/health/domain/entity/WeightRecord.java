package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("weight_records")
public class WeightRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private BigDecimal weight; // 体重(kg)
    
    @TableField("recorded_at")
    private LocalDateTime recordedAt; // 记录时间
    
    private String note; // 备注
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    // BMI字段不存储到数据库，通过计算得出
    @TableField(exist = false)
    private BigDecimal bmi;
}
