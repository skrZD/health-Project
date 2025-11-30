package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.DietRecord;
import com.example.health.infra.mapper.DietRecordMapper;
import org.springframework.stereotype.Service;

@Service
public class DietRecordService extends ServiceImpl<DietRecordMapper, DietRecord> {
}

