package com.example.health.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.health.common.api.ApiResponse;
import com.example.health.domain.entity.BmiCalculation;
import com.example.health.service.BmiCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bmi-calculations")
public class BmiCalculationController {
    
    @Autowired
    private BmiCalculationService bmiCalculationService;
    
    /**
     * 计算BMI并保存记录
     */
    @PostMapping
    public ApiResponse<BmiCalculation> calculate(@RequestHeader("X-User-Id") Long userId,
                                                 @RequestParam BigDecimal height,
                                                 @RequestParam BigDecimal weight,
                                                 @RequestParam(required = false) String note) {
        BmiCalculation calculation = bmiCalculationService.calculateAndSave(userId, height, weight, note);
        return ApiResponse.success(calculation);
    }
    
    /**
     * 获取当前用户的BMI计算历史
     */
    @GetMapping("/my-history")
    public ApiResponse<List<BmiCalculation>> getMyHistory(@RequestHeader("X-User-Id") Long userId) {
        List<BmiCalculation> history = bmiCalculationService.getByUserId(userId);
        return ApiResponse.success(history);
    }
    
    /**
     * 获取BMI计算列表（管理员用）
     */
    @GetMapping
    public ApiResponse<Page<BmiCalculation>> getList(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false) Long userId,
                                                     @RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) {
        Page<BmiCalculation> pageParam = new Page<>(page, size);
        Page<BmiCalculation> result = bmiCalculationService.getList(pageParam, userId, startDate, endDate);
        return ApiResponse.success(result);
    }
    
    /**
     * 删除BMI计算记录
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        boolean success = bmiCalculationService.deleteById(id);
        return success ? ApiResponse.success() : ApiResponse.error("删除失败");
    }
    
    /**
     * 获取BMI统计信息
     */
    @GetMapping("/stats")
    public ApiResponse<Object> getStats() {
        Object stats = bmiCalculationService.getStats();
        return ApiResponse.success(stats);
    }
}
