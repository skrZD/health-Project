package com.example.health.config;

import com.example.health.security.JwtAuthenticationFilter;
import com.example.health.security.JwtAuthenticationProvider;
import com.example.health.security.JwtService;
import com.example.health.security.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF，因为使用JWT
            .csrf().disable()
            // 启用CORS
            .cors().configurationSource(corsConfigurationSource())
            .and()
            // 设置会话管理为无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 配置请求授权
            .authorizeRequests()
                // 允许访问的公开接口
                .antMatchers(
                    "/api/auth/**",                    // 认证相关接口
                    "/api/admin/auth/login",           // 管理员登录接口
                    "/api/admin/auth/refresh",         // 管理员刷新token接口
                    "/api/health",                     // 健康检查
                    "/api/foods/search",               // 食物搜索
                    "/api/food-categories",            // 食物分类
                    "/api/recipes/**",                 // 食谱查询（包含所有子路径）
                    "/api/recipe-categories/**",       // 食谱分类（包含所有子路径）
                    "/api/health-profiles/**",         // 健康档案（包含所有子路径）
                    "/api/bmi-calculations/**",        // BMI计算（包含所有子路径）
                    "/api/test/**",                    // 测试接口（包含图片访问）
                    "/static/**",                      // 静态资源
                    "/docs/**",                        // Swagger文档
                    "/swagger-ui/**",                  // Swagger UI
                    "/v3/api-docs/**",                 // OpenAPI文档
                    "/error"                           // 错误页面
                ).permitAll()
                // 管理员接口需要ADMIN角色
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            .and()
            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }





    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://*.yourdomain.com"
        ));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
