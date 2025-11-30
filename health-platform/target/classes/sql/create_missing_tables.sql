-- 创建缺失的表
-- 运动记录表
CREATE TABLE IF NOT EXISTS exercise_records (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  exercise_type VARCHAR(32) NOT NULL,           -- 运动类型：跑步/游泳/骑行等
  exercise_name VARCHAR(64) NOT NULL,           -- 运动名称
  duration INT UNSIGNED NOT NULL,               -- 运动时长(分钟)
  calories INT UNSIGNED NOT NULL,               -- 消耗卡路里
  exercise_time DATETIME NOT NULL,              -- 运动时间
  note VARCHAR(255) NULL,                       -- 备注
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_exercise_user_time (user_id, exercise_time),
  CONSTRAINT fk_exercise_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 体重记录表
CREATE TABLE IF NOT EXISTS weight_records (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  weight DECIMAL(5,2) NOT NULL,                 -- 体重(kg)
  recorded_at DATETIME NOT NULL,                -- 记录时间
  note VARCHAR(255) NULL,                       -- 备注
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_weight_user_time (user_id, recorded_at),
  CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 为现有表添加缺失的字段
ALTER TABLE foods ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE foods ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 为用户表添加目标字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(64) NULL COMMENT '用户姓名';
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(255) NULL COMMENT '头像URL';
ALTER TABLE users ADD COLUMN IF NOT EXISTS daily_calorie_goal INT UNSIGNED DEFAULT 600 COMMENT '每日热量目标';
ALTER TABLE users ADD COLUMN IF NOT EXISTS daily_exercise_goal INT UNSIGNED DEFAULT 30 COMMENT '每日运动时长目标(分钟)';
ALTER TABLE users ADD COLUMN IF NOT EXISTS daily_step_goal INT UNSIGNED DEFAULT 10000 COMMENT '每日步数目标';

-- 插入一些示例数据
INSERT IGNORE INTO food_categories (id, name, parent_id, sort) VALUES
(1, '主食', NULL, 1),
(2, '肉蛋奶', NULL, 2),
(3, '蔬果', NULL, 3),
(4, '饮品', NULL, 4);

INSERT IGNORE INTO foods (id, category_id, name, alias, unit, calories_per_100, protein_g_per_100, fat_g_per_100, carbs_g_per_100, sodium_mg_per_100, enabled) VALUES
(1, 1, '米饭', '白米饭', 'g', 116, 2.6, 0.3, 25.9, 5, 1),
(2, 1, '面条', '白面条', 'g', 109, 4.0, 0.4, 22.0, 5, 1),
(3, 2, '鸡胸肉', '鸡胸脯肉', 'g', 165, 31.0, 3.6, 0, 74, 1),
(4, 2, '鸡蛋', '鸡蛋', '个', 155, 13.0, 11.0, 1.1, 124, 1),
(5, 3, '西兰花', '绿花菜', 'g', 33, 3.0, 0.4, 6.6, 33, 1),
(6, 3, '苹果', '苹果', 'g', 52, 0.3, 0.2, 13.8, 1, 1),
(7, 4, '牛奶', '纯牛奶', 'ml', 42, 3.0, 1.0, 5.0, 44, 1),
(8, 4, '水', '纯净水', 'ml', 0, 0, 0, 0, 0, 1);

-- 插入管理员用户
INSERT IGNORE INTO admin_users (id, username, password_hash, display_name, status, created_at) VALUES
(1, 'admin', 'admin123', '系统管理员', 1, NOW());
