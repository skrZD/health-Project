package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.Food;
import com.example.health.infra.mapper.FoodMapper;
import org.springframework.stereotype.Service;

@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {
}




