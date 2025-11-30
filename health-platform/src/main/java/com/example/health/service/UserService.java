package com.example.health.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.health.domain.entity.User;
import com.example.health.infra.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    public User findByOpenid(String openid) {
        return lambdaQuery().eq(User::getOpenid, openid).one();
    }
}




