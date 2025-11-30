package com.example.health.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exercise_records")
public class ExerciseRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String exerciseType; // 跑步/游泳/骑行/健身/瑜伽等
    private String exerciseName;
    private Integer duration; // 运动时长(分钟)
    private Integer calories; // 消耗卡路里
    private LocalDateTime exerciseTime; // 运动时间
    private String note; // 备注
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
