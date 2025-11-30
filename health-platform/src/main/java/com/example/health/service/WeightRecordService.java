package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.WeightRecord;
import com.example.health.infra.mapper.WeightRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class WeightRecordService extends ServiceImpl<WeightRecordMapper, WeightRecord> {
}

