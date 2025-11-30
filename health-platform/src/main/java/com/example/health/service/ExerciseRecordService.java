package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.ExerciseRecord;
import com.example.health.infra.mapper.ExerciseRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class ExerciseRecordService extends ServiceImpl<ExerciseRecordMapper, ExerciseRecord> {
}

