# 后端API修复指南

## 问题分析

根据错误信息，存在两个主要问题：

### 1. 数据类型转换错误
```
Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'
```

### 2. 查询结果过多异常
```
nested exception is org.apache.ibatis.exceptions.TooManyResultsException: Expected one result (or null) to be returned by selectOne(), but found: 2
```

## 修复方案

### 问题1：String转Long错误

**原因分析：**
- 前端传递的参数可能是字符串类型，但后端期望Long类型
- 可能是路径参数或查询参数的转换问题

**修复方法：**

1. **Controller层修复：**
```java
@GetMapping("/admin/users/stats")
public ResponseEntity<Map<String, Object>> getUserStats() {
    try {
        Map<String, Object> stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
}
```

2. **Service层修复：**
```java
public Map<String, Object> getUserStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", userMapper.getTotalUsers());
    stats.put("newUsersToday", userMapper.getNewUsersToday());
    return stats;
}
```

3. **Mapper层修复：**
```xml
<select id="getTotalUsers" resultType="Long">
    SELECT COUNT(*) FROM users
</select>

<select id="getNewUsersToday" resultType="Long">
    SELECT COUNT(*) FROM users 
    WHERE DATE(created_at) = CURDATE()
</select>
```

### 问题2：selectOne返回多个结果

**原因分析：**
- 使用了`selectOne()`但查询返回了多条记录
- 应该使用`selectList()`或者修改查询条件

**修复方法：**

1. **使用selectList：**
```java
public List<User> getRecentUsers() {
    return userMapper.selectList(
        new QueryWrapper<User>()
            .orderByDesc("created_at")
            .last("LIMIT 5")
    );
}
```

2. **或者使用聚合查询：**
```java
public Map<String, Object> getUserStats() {
    Map<String, Object> stats = new HashMap<>();
    
    // 使用聚合查询避免多条记录
    stats.put("totalUsers", userMapper.selectCount(null));
    stats.put("newUsersToday", userMapper.selectCount(
        new QueryWrapper<User>()
            .apply("DATE(created_at) = CURDATE()")
    ));
    
    return stats;
}
```

## 推荐的API接口设计

### 1. 用户统计接口
```java
@GetMapping("/admin/users/stats")
public ResponseEntity<Map<String, Object>> getUserStats() {
    Map<String, Object> result = new HashMap<>();
    result.put("totalUsers", userService.getTotalUsers());
    result.put("newUsersToday", userService.getNewUsersToday());
    result.put("recentUsers", userService.getRecentUsers(5));
    return ResponseEntity.ok(result);
}
```

### 2. 饮食记录统计接口
```java
@GetMapping("/admin/diet-records/stats")
public ResponseEntity<Map<String, Object>> getDietRecordStats() {
    Map<String, Object> result = new HashMap<>();
    result.put("totalDietRecords", dietRecordService.getTotalCount());
    result.put("todayDietRecords", dietRecordService.getTodayCount());
    result.put("recentRecords", dietRecordService.getRecentRecords(5));
    return ResponseEntity.ok(result);
}
```

### 3. 运动记录统计接口
```java
@GetMapping("/admin/exercise-records/stats")
public ResponseEntity<Map<String, Object>> getExerciseRecordStats() {
    Map<String, Object> result = new HashMap<>();
    result.put("totalExerciseRecords", exerciseRecordService.getTotalCount());
    result.put("todayExerciseRecords", exerciseRecordService.getTodayCount());
    result.put("recentRecords", exerciseRecordService.getRecentRecords(5));
    return ResponseEntity.ok(result);
}
```

## 数据库查询优化

### 1. 使用索引
```sql
-- 为created_at字段添加索引
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_diet_records_created_at ON diet_records(created_at);
CREATE INDEX idx_exercise_records_created_at ON exercise_records(created_at);
```

### 2. 优化统计查询
```sql
-- 用户统计查询
SELECT 
    COUNT(*) as totalUsers,
    COUNT(CASE WHEN DATE(created_at) = CURDATE() THEN 1 END) as newUsersToday
FROM users;

-- 饮食记录统计查询
SELECT 
    COUNT(*) as totalDietRecords,
    COUNT(CASE WHEN DATE(created_at) = CURDATE() THEN 1 END) as todayDietRecords
FROM diet_records;
```

## 前端调用优化

当前前端已经添加了错误处理，使用模拟数据作为备选方案：

```javascript
// 加载统计数据
const loadStats = async () => {
  try {
    loading.value = true
    // 暂时注释掉API调用，使用模拟数据
    // const [userStatsResult, dietStatsResult, exerciseStatsResult] = await Promise.all([
    //   getUserStats(),
    //   getDietRecordStats(),
    //   getExerciseRecordStats()
    // ])
    
    // 使用模拟数据
    Object.assign(stats, {
      totalUsers: 1256,
      newUsersToday: 23,
      totalDietRecords: 3420,
      todayDietRecords: 156,
      totalExerciseRecords: 2180,
      todayExerciseRecords: 89,
      totalWeightRecords: 1250,
      todayWeightRecords: 45,
      totalRecipes: 320,
      todayRecipes: 8,
      totalFoods: 1850,
      todayFoods: 12,
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 错误处理逻辑
  } finally {
    loading.value = false
  }
}
```

## 总结

1. **后端修复重点：**
   - 修复数据类型转换问题
   - 解决selectOne返回多条记录的问题
   - 优化数据库查询

2. **前端已优化：**
   - 添加了完整的图表功能
   - 实现了图表图例和数据展示
   - 添加了错误处理和模拟数据

3. **后续工作：**
   - 后端修复完成后，取消前端API调用的注释
   - 根据实际数据结构调整前端数据处理逻辑

