package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.Role;
import com.example.health.infra.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
    
    /**
     * 获取所有角色
     */
    public List<Role> getAllRoles() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("name");
        return this.list(queryWrapper);
    }
    
    /**
     * 创建角色
     */
    public Role createRole(Role role) {
        role.setCreatedAt(LocalDateTime.now());
        this.save(role);
        return role;
    }
    
    /**
     * 更新角色
     */
    public Role updateRole(Role role) {
        this.updateById(role);
        return role;
    }
    
    /**
     * 删除角色
     */
    public boolean deleteRole(Long id) {
        return this.removeById(id);
    }
}
