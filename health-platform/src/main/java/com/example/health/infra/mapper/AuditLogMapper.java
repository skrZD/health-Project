package com.example.health.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.health.domain.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
