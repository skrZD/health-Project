package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.domain.entity.HealthProfile;
import com.example.health.infra.mapper.HealthProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthProfileService {
    
    @Autowired
    private HealthProfileMapper healthProfileMapper;
    
    /**
     * 根据用户ID获取健康档案
     */
    public HealthProfile getByUserId(Long userId) {
        QueryWrapper<HealthProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return healthProfileMapper.selectOne(queryWrapper);
    }
    
    /**
     * 创建或更新健康档案
     */
    public HealthProfile saveOrUpdate(HealthProfile healthProfile) {
        HealthProfile existing = getByUserId(healthProfile.getUserId());
        
        if (existing != null) {
            healthProfile.setId(existing.getId());
            healthProfile.setUpdatedAt(LocalDateTime.now());
            healthProfileMapper.updateById(healthProfile);
        } else {
            healthProfile.setCreatedAt(LocalDateTime.now());
            healthProfile.setUpdatedAt(LocalDateTime.now());
            healthProfileMapper.insert(healthProfile);
        }
        
        return healthProfile;
    }
    
    /**
     * 计算并更新BMI
     */
    public HealthProfile calculateAndUpdateBmi(Long userId, BigDecimal height, BigDecimal weight) {
        HealthProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = new HealthProfile();
            profile.setUserId(userId);
            profile.setCreatedAt(LocalDateTime.now());
        }
        
        profile.setHeight(height);
        profile.setWeight(weight);
        
        // 计算BMI: BMI = 体重(kg) / 身高(m)²
        if (height != null && weight != null && height.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bmi = weight.divide(heightInMeters.multiply(heightInMeters), 2, BigDecimal.ROUND_HALF_UP);
            profile.setBmi(bmi);
        }
        
        profile.setUpdatedAt(LocalDateTime.now());
        return saveOrUpdate(profile);
    }
    
    /**
     * 获取健康档案列表（管理员用）
     */
    public Page<HealthProfile> getList(Page<HealthProfile> page, String keyword) {
        QueryWrapper<HealthProfile> queryWrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("medical_history", keyword)
                       .or()
                       .like("allergies", keyword)
                       .or()
                       .like("emergency_contact", keyword);
        }
        queryWrapper.orderByDesc("updated_at");
        return healthProfileMapper.selectPage(page, queryWrapper);
    }
    
    /**
     * 删除健康档案
     */
    public boolean deleteById(Long id) {
        return healthProfileMapper.deleteById(id) > 0;
    }
}
