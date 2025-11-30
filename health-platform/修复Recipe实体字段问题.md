# 🔧 修复Recipe实体字段问题

## ❌ 问题描述

错误信息显示：
```
JSON parse error: Unrecognized field "difficultyLevel" (class com.example.health.domain.entity.Recipe), not marked as ignorable
```

## 🔍 问题原因

`Recipe` 实体类缺少前端发送的字段：
- `imageUrl` - 图片URL
- `difficultyLevel` - 难度等级
- `cookingTime` - 烹饪时间
- `servings` - 份数

## ✅ 解决方案

### 1. 已修复Recipe实体类

在 `Recipe.java` 中添加了缺失的字段：

```java
@Data
@TableName("recipes")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private Integer enabled;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 新增字段
    private String imageUrl;
    private String difficultyLevel;
    private Integer cookingTime;
    private Integer servings;
}
```

### 2. 需要执行数据库脚本

执行以下SQL语句来添加数据库字段：

```sql
-- 添加图片URL字段
ALTER TABLE recipes ADD COLUMN image_url VARCHAR(255) NULL COMMENT '食谱图片';

-- 添加难度等级字段
ALTER TABLE recipes ADD COLUMN difficulty_level ENUM('简单','中等','困难') DEFAULT '简单' COMMENT '难度等级';

-- 添加烹饪时间字段
ALTER TABLE recipes ADD COLUMN cooking_time INT UNSIGNED NULL COMMENT '烹饪时间(分钟)';

-- 添加份数字段
ALTER TABLE recipes ADD COLUMN servings INT UNSIGNED DEFAULT 1 COMMENT '份数';
```

### 3. 重启后端服务

修改实体类后需要重启Spring Boot应用。

## 🚀 测试步骤

1. **执行数据库脚本**：
   ```sql
   -- 在MySQL中执行上述SQL语句
   ```

2. **重启后端服务**：
   ```bash
   cd D:\JetBrains\IdeaProjects\health-platform
   mvn spring-boot:run
   ```

3. **测试图片上传**：
   - 打开前端页面
   - 进入食谱管理
   - 创建新食谱并上传图片
   - 保存应该成功

## 📋 字段映射

| 前端字段 | 后端字段 | 数据库字段 | 类型 |
|---------|---------|-----------|------|
| imageUrl | imageUrl | image_url | VARCHAR(255) |
| difficultyLevel | difficultyLevel | difficulty_level | ENUM |
| cookingTime | cookingTime | cooking_time | INT |
| servings | servings | servings | INT |

## ✅ 验证方法

1. 检查数据库表结构：
   ```sql
   DESCRIBE recipes;
   ```

2. 测试API接口：
   ```bash
   curl -X POST http://localhost:8088/api/admin/recipes \
     -H "Content-Type: application/json" \
     -d '{
       "name": "测试食谱",
       "description": "测试描述",
       "difficultyLevel": "简单",
       "cookingTime": 30,
       "servings": 2,
       "enabled": 1
     }'
   ```

现在图片上传功能应该可以正常工作了！


