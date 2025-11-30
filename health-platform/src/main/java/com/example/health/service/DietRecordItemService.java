package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.DietRecordItem;
import com.example.health.infra.mapper.DietRecordItemMapper;
import org.springframework.stereotype.Service;

@Service
public class DietRecordItemService extends ServiceImpl<DietRecordItemMapper, DietRecordItem> {
}

