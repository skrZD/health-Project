package com.example.health.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.HealthProfile;
import com.example.health.service.HealthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/health-profiles")
public class HealthProfileController {
    
    @Autowired
    private HealthProfileService healthProfileService;
    
    /**
     * 获取当前用户的健康档案
     */
    @GetMapping("/me")
    public ApiResponse<HealthProfile> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        HealthProfile profile = healthProfileService.getByUserId(userId);
        return ApiResponse.success(profile);
    }
    
    /**
     * 创建或更新健康档案
     */
    @PostMapping
    public ApiResponse<HealthProfile> saveProfile(@RequestHeader("X-User-Id") Long userId, 
                                                  @RequestBody HealthProfile healthProfile) {
        healthProfile.setUserId(userId);
        HealthProfile saved = healthProfileService.saveOrUpdate(healthProfile);
        return ApiResponse.success(saved);
    }
    
    /**
     * 计算并更新BMI
     */
    @PostMapping("/bmi")
    public ApiResponse<HealthProfile> calculateBmi(@RequestHeader("X-User-Id") Long userId,
                                                   @RequestParam BigDecimal height,
                                                   @RequestParam BigDecimal weight) {
        HealthProfile profile = healthProfileService.calculateAndUpdateBmi(userId, height, weight);
        return ApiResponse.success(profile);
    }
    
    /**
     * 获取健康档案列表（管理员用）
     */
    @GetMapping
    public ApiResponse<Page<HealthProfile>> getList(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String keyword) {
        Page<HealthProfile> pageParam = new Page<>(page, size);
        Page<HealthProfile> result = healthProfileService.getList(pageParam, keyword);
        return ApiResponse.success(result);
    }
    
    /**
     * 获取健康档案详情
     */
    @GetMapping("/{id}")
    public ApiResponse<HealthProfile> getById(@PathVariable Long id) {
        HealthProfile profile = healthProfileService.getByUserId(id);
        return ApiResponse.success(profile);
    }
    
    /**
     * 删除健康档案
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        boolean success = healthProfileService.deleteById(id);
        return success ? ApiResponse.success() : ApiResponse.error("删除失败");
    }
}
