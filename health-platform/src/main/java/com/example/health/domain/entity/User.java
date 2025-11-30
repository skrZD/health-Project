package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String unionid;
    private String nickname;
    private String name; // 用户姓名
    private String avatar; // 头像URL
    private String gender; // 男/女/其他
    private Integer age;
    private Double height; // 身高(cm)
    private Double weight; // 体重(kg)
    
    // 用户目标
    private Integer dailyCalorieGoal; // 每日热量目标
    private Integer dailyExerciseGoal; // 每日运动时长目标(分钟)
    private Integer dailyStepGoal; // 每日步数目标
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



