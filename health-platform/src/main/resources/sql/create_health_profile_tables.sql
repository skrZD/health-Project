-- 创建健康档案表
CREATE TABLE IF NOT EXISTS health_profiles (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  height DECIMAL(5,2) NULL COMMENT '身高(cm)',
  weight DECIMAL(5,2) NULL COMMENT '当前体重(kg)',
  bmi DECIMAL(4,2) NULL COMMENT 'BMI指数',
  blood_pressure_systolic INT NULL COMMENT '收缩压',
  blood_pressure_diastolic INT NULL COMMENT '舒张压',
  blood_sugar DECIMAL(4,2) NULL COMMENT '血糖值',
  cholesterol DECIMAL(4,2) NULL COMMENT '胆固醇值',
  medical_history TEXT NULL COMMENT '病史',
  allergies TEXT NULL COMMENT '过敏史',
  medications TEXT NULL COMMENT '用药情况',
  emergency_contact VARCHAR(64) NULL COMMENT '紧急联系人',
  emergency_phone VARCHAR(20) NULL COMMENT '紧急联系电话',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_health_profile_user (user_id),
  CONSTRAINT fk_health_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建BMI计算记录表
CREATE TABLE IF NOT EXISTS bmi_calculations (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  height DECIMAL(5,2) NOT NULL COMMENT '身高(cm)',
  weight DECIMAL(5,2) NOT NULL COMMENT '体重(kg)',
  bmi DECIMAL(4,2) NOT NULL COMMENT 'BMI指数',
  bmi_category VARCHAR(20) NOT NULL COMMENT 'BMI分类',
  calculated_at DATETIME NOT NULL COMMENT '计算时间',
  note VARCHAR(255) NULL COMMENT '备注',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_bmi_user_time (user_id, calculated_at),
  CONSTRAINT fk_bmi_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 更新饮食记录明细表，添加食谱支持
ALTER TABLE diet_record_items 
ADD COLUMN recipe_id BIGINT UNSIGNED NULL COMMENT '食谱ID' AFTER food_id,
ADD INDEX idx_diet_item_recipe (recipe_id),
ADD CONSTRAINT fk_diet_item_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE RESTRICT;

-- 更新用户表，添加健康档案关联
ALTER TABLE users 
ADD COLUMN health_profile_id BIGINT UNSIGNED NULL COMMENT '健康档案ID' AFTER daily_step_goal,
ADD INDEX idx_user_health_profile (health_profile_id),
ADD CONSTRAINT fk_user_health_profile FOREIGN KEY (health_profile_id) REFERENCES health_profiles(id) ON DELETE SET NULL;

-- 插入BMI分类数据
INSERT IGNORE INTO bmi_calculations (id, user_id, height, weight, bmi, bmi_category, calculated_at, note) VALUES
(0, 0, 0, 0, 0, 'UNDERWEIGHT', '1970-01-01 00:00:00', 'BMI < 18.5 偏瘦'),
(0, 0, 0, 0, 18.5, 'NORMAL', '1970-01-01 00:00:00', '18.5 ≤ BMI < 24 正常'),
(0, 0, 0, 0, 24, 'OVERWEIGHT', '1970-01-01 00:00:00', '24 ≤ BMI < 28 超重'),
(0, 0, 0, 0, 28, 'OBESE', '1970-01-01 00:00:00', 'BMI ≥ 28 肥胖');


