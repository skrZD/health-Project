-- 修复食谱表字段
-- 如果字段已存在，会忽略错误

-- 添加图片URL字段
ALTER TABLE recipes ADD COLUMN image_url VARCHAR(255) NULL COMMENT '食谱图片';

-- 添加难度等级字段
ALTER TABLE recipes ADD COLUMN difficulty_level ENUM('简单','中等','困难') DEFAULT '简单' COMMENT '难度等级';

-- 添加烹饪时间字段
ALTER TABLE recipes ADD COLUMN cooking_time INT UNSIGNED NULL COMMENT '烹饪时间(分钟)';

-- 添加份数字段
ALTER TABLE recipes ADD COLUMN servings INT UNSIGNED DEFAULT 1 COMMENT '份数';

