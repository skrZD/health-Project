package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_profiles")
public class HealthProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    private BigDecimal height; // 身高(cm)
    private BigDecimal weight; // 当前体重(kg)
    private BigDecimal bmi; // BMI指数
    
    @TableField("blood_pressure_systolic")
    private Integer bloodPressureSystolic; // 收缩压
    
    @TableField("blood_pressure_diastolic")
    private Integer bloodPressureDiastolic; // 舒张压
    
    @TableField("blood_sugar")
    private BigDecimal bloodSugar; // 血糖值
    
    private BigDecimal cholesterol; // 胆固醇值
    
    @TableField("medical_history")
    private String medicalHistory; // 病史
    
    private String allergies; // 过敏史
    
    private String medications; // 用药情况
    
    @TableField("emergency_contact")
    private String emergencyContact; // 紧急联系人
    
    @TableField("emergency_phone")
    private String emergencyPhone; // 紧急联系电话
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
