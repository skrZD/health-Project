package com.example.health.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "mySecretKey123456789012345678901234567890123456789012345678901234567890";
    private int expireMinutes = 120; // 2小时
}