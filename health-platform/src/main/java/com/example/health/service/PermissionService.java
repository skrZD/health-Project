package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.Permission;
import com.example.health.infra.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {
    
    /**
     * 获取所有权限
     */
    public List<Permission> getAllPermissions() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("code");
        return this.list(queryWrapper);
    }
    
    /**
     * 创建权限
     */
    public Permission createPermission(Permission permission) {
        permission.setCreatedAt(LocalDateTime.now());
        this.save(permission);
        return permission;
    }
    
    /**
     * 更新权限
     */
    public Permission updatePermission(Permission permission) {
        this.updateById(permission);
        return permission;
    }
    
    /**
     * 删除权限
     */
    public boolean deletePermission(Long id) {
        return this.removeById(id);
    }
    
    /**
     * 根据角色ID获取权限
     */
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        // 这里需要联表查询，暂时返回空列表
        // 实际实现中需要自定义SQL查询
        return new ArrayList<>();
    }
}
