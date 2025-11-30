package com.example.health.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.health.domain.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
}
