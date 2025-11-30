# 仪表盘API接口规范

## 概述

仪表盘需要调用以下6个统计API接口来获取真实的数据库数据。每个接口都应该返回相应的统计数据。

## API接口列表

### 1. 用户统计接口

**接口地址：** `GET /admin/users/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalUsers": 1256,
    "newUsersToday": 23
  }
}
```

**实现建议：**
```sql
-- 获取总用户数
SELECT COUNT(*) as totalUsers FROM users WHERE enabled = 1;

-- 获取今日新增用户数
SELECT COUNT(*) as newUsersToday FROM users 
WHERE DATE(created_at) = CURDATE() AND enabled = 1;
```

### 2. 饮食记录统计接口

**接口地址：** `GET /admin/diet-records/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalDietRecords": 3420,
    "todayDietRecords": 156
  }
}
```

**实现建议：**
```sql
-- 获取总饮食记录数
SELECT COUNT(*) as totalDietRecords FROM diet_records;

-- 获取今日新增饮食记录数
SELECT COUNT(*) as todayDietRecords FROM diet_records 
WHERE DATE(created_at) = CURDATE();
```

### 3. 运动记录统计接口

**接口地址：** `GET /admin/exercise-records/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalExerciseRecords": 2180,
    "todayExerciseRecords": 89
  }
}
```

**实现建议：**
```sql
-- 获取总运动记录数
SELECT COUNT(*) as totalExerciseRecords FROM exercise_records;

-- 获取今日新增运动记录数
SELECT COUNT(*) as todayExerciseRecords FROM exercise_records 
WHERE DATE(created_at) = CURDATE();
```

### 4. 体重记录统计接口

**接口地址：** `GET /admin/weight-records/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalWeightRecords": 1250,
    "todayWeightRecords": 45
  }
}
```

**实现建议：**
```sql
-- 获取总体重记录数
SELECT COUNT(*) as totalWeightRecords FROM weight_records;

-- 获取今日新增体重记录数
SELECT COUNT(*) as todayWeightRecords FROM weight_records 
WHERE DATE(created_at) = CURDATE();
```

### 5. 食物统计接口

**接口地址：** `GET /admin/foods/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalFoods": 1850,
    "todayFoods": 12
  }
}
```

**实现建议：**
```sql
-- 获取总食物数
SELECT COUNT(*) as totalFoods FROM foods WHERE enabled = 1;

-- 获取今日新增食物数
SELECT COUNT(*) as todayFoods FROM foods 
WHERE DATE(created_at) = CURDATE() AND enabled = 1;
```

### 6. 食谱统计接口

**接口地址：** `GET /admin/recipes/stats`

**返回数据结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRecipes": 320,
    "todayRecipes": 8
  }
}
```

**实现建议：**
```sql
-- 获取总食谱数
SELECT COUNT(*) as totalRecipes FROM recipes WHERE enabled = 1;

-- 获取今日新增食谱数
SELECT COUNT(*) as todayRecipes FROM recipes 
WHERE DATE(created_at) = CURDATE() AND enabled = 1;
```

## 后端实现示例

### Controller层示例

```java
@RestController
@RequestMapping("/admin")
public class StatsController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DietRecordService dietRecordService;
    
    @Autowired
    private ExerciseRecordService exerciseRecordService;
    
    @Autowired
    private WeightRecordService weightRecordService;
    
    @Autowired
    private FoodService foodService;
    
    @Autowired
    private RecipeService recipeService;
    
    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = userService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/diet-records/stats")
    public ResponseEntity<Map<String, Object>> getDietRecordStats() {
        try {
            Map<String, Object> stats = dietRecordService.getStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    // ... 其他统计接口
}
```

### Service层示例

```java
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.getTotalUsers());
        stats.put("newUsersToday", userMapper.getNewUsersToday());
        return stats;
    }
}
```

### Mapper层示例

```java
@Mapper
public interface UserMapper {
    
    @Select("SELECT COUNT(*) FROM users WHERE enabled = 1")
    Long getTotalUsers();
    
    @Select("SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURDATE() AND enabled = 1")
    Long getNewUsersToday();
}
```

## 错误处理

前端已经实现了完善的错误处理机制：

1. **使用 `Promise.allSettled()`** 确保即使某些API失败，其他API仍能正常执行
2. **每个API都有独立的错误处理**，失败时会在控制台输出警告信息
3. **提供默认值**，确保界面不会因为数据缺失而崩溃
4. **优雅降级**，即使所有API都失败，界面仍能正常显示（显示0值）

## 数据列表接口

仪表盘还需要调用以下数据列表接口来获取最近的数据：

- `GET /admin/users?page=1&size=5` - 获取最近5个用户
- `GET /admin/diet-records?page=1&size=5` - 获取最近5条饮食记录
- `GET /admin/exercise-records?page=1&size=5` - 获取最近5条运动记录

这些接口应该按创建时间倒序返回数据。

## 注意事项

1. **数据一致性**：确保统计数据与列表数据的一致性
2. **性能优化**：建议对统计查询添加适当的数据库索引
3. **缓存策略**：可以考虑对统计数据添加短期缓存以提高性能
4. **权限控制**：确保只有管理员能访问这些统计接口

