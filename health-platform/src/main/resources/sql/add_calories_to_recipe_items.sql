-- 为recipe_items表添加calories字段
ALTER TABLE recipe_items ADD COLUMN calories DECIMAL(8,2) NULL COMMENT '热量（千卡）' AFTER quantity_unit;

