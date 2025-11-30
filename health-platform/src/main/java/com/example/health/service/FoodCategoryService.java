package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.FoodCategory;
import com.example.health.infra.mapper.FoodCategoryMapper;
import org.springframework.stereotype.Service;

@Service
public class FoodCategoryService extends ServiceImpl<FoodCategoryMapper, FoodCategory> {
}

