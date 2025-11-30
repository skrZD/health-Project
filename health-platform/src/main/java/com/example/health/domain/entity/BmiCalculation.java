package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bmi_calculations")
public class BmiCalculation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private BigDecimal height; // 身高(cm)
    private BigDecimal weight; // 体重(kg)
    private BigDecimal bmi; // BMI指数
    
    @TableField("bmi_category")
    private String bmiCategory; // BMI分类
    
    @TableField("calculated_at")
    private LocalDateTime calculatedAt; // 计算时间
    
    private String note; // 备注
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}
