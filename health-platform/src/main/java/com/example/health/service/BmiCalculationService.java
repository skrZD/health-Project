package com.example.health.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.domain.entity.BmiCalculation;
import com.example.health.infra.mapper.BmiCalculationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BmiCalculationService {
    
    @Autowired
    private BmiCalculationMapper bmiCalculationMapper;
    
    /**
     * 计算BMI并保存记录
     */
    public BmiCalculation calculateAndSave(Long userId, BigDecimal height, BigDecimal weight, String note) {
        BmiCalculation calculation = new BmiCalculation();
        calculation.setUserId(userId);
        calculation.setHeight(height);
        calculation.setWeight(weight);
        calculation.setCalculatedAt(LocalDateTime.now());
        calculation.setNote(note);
        calculation.setCreatedAt(LocalDateTime.now());
        
        // 计算BMI
        if (height != null && weight != null && height.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal heightInMeters = height.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal bmi = weight.divide(heightInMeters.multiply(heightInMeters), 2, BigDecimal.ROUND_HALF_UP);
            calculation.setBmi(bmi);
            calculation.setBmiCategory(getBmiCategory(bmi));
        }
        
        bmiCalculationMapper.insert(calculation);
        return calculation;
    }
    
    /**
     * 根据BMI值获取分类
     */
    private String getBmiCategory(BigDecimal bmi) {
        if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
            return "UNDERWEIGHT";
        } else if (bmi.compareTo(new BigDecimal("24")) < 0) {
            return "NORMAL";
        } else if (bmi.compareTo(new BigDecimal("28")) < 0) {
            return "OVERWEIGHT";
        } else {
            return "OBESE";
        }
    }
    
    /**
     * 获取用户的BMI计算历史
     */
    public List<BmiCalculation> getByUserId(Long userId) {
        QueryWrapper<BmiCalculation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("calculated_at");
        return bmiCalculationMapper.selectList(queryWrapper);
    }
    
    /**
     * 获取BMI计算列表（管理员用）
     */
    public Page<BmiCalculation> getList(Page<BmiCalculation> page, Long userId, String startDate, String endDate) {
        QueryWrapper<BmiCalculation> queryWrapper = new QueryWrapper<>();
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (startDate != null && !startDate.trim().isEmpty()) {
            queryWrapper.ge("calculated_at", startDate);
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            queryWrapper.le("calculated_at", endDate);
        }
        queryWrapper.orderByDesc("calculated_at");
        return bmiCalculationMapper.selectPage(page, queryWrapper);
    }
    
    /**
     * 删除BMI计算记录
     */
    public boolean deleteById(Long id) {
        return bmiCalculationMapper.deleteById(id) > 0;
    }
    
    /**
     * 获取BMI统计信息
     */
    public Object getStats() {
        // 这里可以添加统计逻辑，比如平均BMI、BMI分布等
        return null;
    }
}
