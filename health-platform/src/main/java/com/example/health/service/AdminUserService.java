package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.AdminUser;
import com.example.health.infra.mapper.AdminUserMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminUserService extends ServiceImpl<AdminUserMapper, AdminUser> {
    public AdminUser findByUsername(String username) {
        return lambdaQuery().eq(AdminUser::getUsername, username).one();
    }
}

