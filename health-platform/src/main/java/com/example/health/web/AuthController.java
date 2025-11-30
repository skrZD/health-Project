package com.example.health.web;

import com.example.health.common.api.ApiResponse;
import com.example.health.config.WeixinProperties;
import com.example.health.domain.entity.User;
import com.example.health.security.JwtService;
import com.example.health.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;
    private final WeixinProperties weixinProperties;
    private final ObjectMapper objectMapper;

    public AuthController(UserService userService, JwtService jwtService, RestTemplate restTemplate, WeixinProperties weixinProperties, ObjectMapper objectMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
        this.weixinProperties = weixinProperties;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/weixin/login")
    @Operation(summary = "微信小程序登录")
    public ApiResponse<?> weixinLogin(@RequestBody WeixinLoginRequest request) {
        try {
            // 1. 通过code获取openid和session_key
            WeixinSessionResponse sessionResponse = getWeixinSession(request.getCode());
            if (sessionResponse == null || sessionResponse.getOpenid() == null) {
                return ApiResponse.fail(400, "微信登录失败，请重试");
            }
            
            // 2. 查找或创建用户
            User user = userService.findByOpenid(sessionResponse.getOpenid());
            if (user == null) {
                user = new User();
                user.setOpenid(sessionResponse.getOpenid());
                user.setUnionid(sessionResponse.getUnionid());
                user.setNickname("用户" + sessionResponse.getOpenid().substring(Math.max(0, sessionResponse.getOpenid().length()-4)));
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                userService.save(user);
            } else {
                // 更新最后登录时间
                user.setUpdatedAt(LocalDateTime.now());
                userService.updateById(user);
            }
            
            // 3. 生成JWT token
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("platform", "weixin");
            claims.put("openid", sessionResponse.getOpenid());
            String token = jwtService.generateToken(user.getId(), "weixin", claims);
            
            // 4. 返回登录结果
            HashMap<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            data.put("expiresIn", 7200); // 2小时
            
            return ApiResponse.ok(data);
        } catch (Exception e) {
            return ApiResponse.fail(500, "登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取微信session信息
     */
    private WeixinSessionResponse getWeixinSession(String code) {
        try {
            // 检查配置
            if (weixinProperties.getAppId() == null || weixinProperties.getAppId().isEmpty() || 
                weixinProperties.getAppSecret() == null || weixinProperties.getAppSecret().isEmpty()) {
                throw new RuntimeException("微信小程序配置未设置，请在application.yml中配置weixin.app-id和weixin.app-secret");
            }

            // 构建请求URL
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weixinProperties.getAppId(), weixinProperties.getAppSecret(), code
            );

            // 发送HTTP请求，返回String类型
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                throw new RuntimeException("微信API返回空响应");
            }

            // 打印响应内容用于调试
            System.out.println("微信API响应: " + responseBody);

            // 手动解析JSON响应
            WeixinSessionResponse sessionResponse = objectMapper.readValue(responseBody, WeixinSessionResponse.class);

            // 检查是否有错误
            if (sessionResponse.getErrcode() != null && sessionResponse.getErrcode() != 0) {
                throw new RuntimeException("微信API错误: " + sessionResponse.getErrcode() + " - " + sessionResponse.getErrmsg());
            }

            return sessionResponse;

        } catch (Exception e) {
            // 如果调用微信API失败，记录错误并返回null
            System.err.println("调用微信API失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // 微信登录请求DTO
    @Data
    public static class WeixinLoginRequest {
        @NotBlank(message = "code不能为空")
        private String code;
    }
    
    // 微信session响应DTO
    @Data
    public static class WeixinSessionResponse {
        private String openid;
        @JsonProperty("session_key")
        private String sessionKey;
        private String unionid;
        private Integer errcode;
        private String errmsg;
    }

    @GetMapping("/me")
    @Operation(summary = "校验登录有效性")
    public ApiResponse<String> me() {
        return ApiResponse.ok("ok");
    }
}



