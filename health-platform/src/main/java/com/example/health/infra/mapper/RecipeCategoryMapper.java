package com.example.health.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.health.domain.entity.RecipeCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeCategoryMapper extends BaseMapper<RecipeCategory> {
}
