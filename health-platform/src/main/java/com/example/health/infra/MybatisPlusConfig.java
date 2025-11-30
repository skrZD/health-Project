package com.example.health.infra;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.health.**.mapper")
public class MybatisPlusConfig {
}




