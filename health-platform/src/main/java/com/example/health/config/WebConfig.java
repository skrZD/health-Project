package com.example.health.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源访问路径
        String uploadPath = Paths.get("uploads").toAbsolutePath().toString();
        System.out.println("Static resource path: " + uploadPath);
        
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + uploadPath + "/")
                .setCachePeriod(0); // 禁用缓存，便于调试
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/static/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
