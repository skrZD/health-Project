package com.example.health.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.health.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
