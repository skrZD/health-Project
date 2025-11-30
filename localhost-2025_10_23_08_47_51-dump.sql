-- MySQL dump 10.13  Distrib 8.0.18, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: health
-- ------------------------------------------------------
-- Server version	8.0.18

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_user_roles`
--

DROP TABLE IF EXISTS `admin_user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_user_roles` (
  `admin_user_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`admin_user_id`,`role_id`),
  KEY `fk_aur_role` (`role_id`),
  CONSTRAINT `fk_aur_admin` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_aur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_user_roles`
--

LOCK TABLES `admin_user_roles` WRITE;
/*!40000 ALTER TABLE `admin_user_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_users`
--

DROP TABLE IF EXISTS `admin_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `display_name` varchar(64) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `last_login_at` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_users`
--

LOCK TABLES `admin_users` WRITE;
/*!40000 ALTER TABLE `admin_users` DISABLE KEYS */;
INSERT INTO `admin_users` VALUES (1,'admin','admin123','系统管理员',1,'2025-10-17 21:00:00','2025-09-20 05:16:25');
/*!40000 ALTER TABLE `admin_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `admin_user_id` bigint(20) unsigned DEFAULT NULL,
  `action` varchar(64) NOT NULL,
  `resource` varchar(64) NOT NULL,
  `resource_id` varchar(64) DEFAULT NULL,
  `detail` json DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_audit_res` (`resource`,`resource_id`),
  KEY `fk_audit_admin` (`admin_user_id`),
  CONSTRAINT `fk_audit_admin` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bmi_calculations`
--

DROP TABLE IF EXISTS `bmi_calculations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bmi_calculations` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `height` decimal(5,2) NOT NULL COMMENT '身高(cm)',
  `weight` decimal(5,2) NOT NULL COMMENT '体重(kg)',
  `bmi` decimal(4,2) NOT NULL COMMENT 'BMI指数',
  `bmi_category` varchar(20) NOT NULL COMMENT 'BMI分类',
  `calculated_at` datetime NOT NULL COMMENT '计算时间',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_bmi_user_time` (`user_id`,`calculated_at`),
  CONSTRAINT `fk_bmi_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bmi_calculations`
--

LOCK TABLES `bmi_calculations` WRITE;
/*!40000 ALTER TABLE `bmi_calculations` DISABLE KEYS */;
/*!40000 ALTER TABLE `bmi_calculations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_record_items`
--

DROP TABLE IF EXISTS `diet_record_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_record_items` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `record_id` bigint(20) unsigned NOT NULL,
  `food_id` bigint(20) unsigned DEFAULT NULL,
  `recipe_id` bigint(20) unsigned DEFAULT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `quantity_unit` varchar(16) NOT NULL,
  `calories` int(10) unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_diet_item_record` (`record_id`),
  KEY `idx_diet_item_food` (`food_id`),
  KEY `idx_diet_item_recipe` (`recipe_id`),
  CONSTRAINT `fk_diet_item_food` FOREIGN KEY (`food_id`) REFERENCES `foods` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_diet_item_recipe` FOREIGN KEY (`recipe_id`) REFERENCES `recipes` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_diet_item_record` FOREIGN KEY (`record_id`) REFERENCES `diet_records` (`id`) ON DELETE CASCADE,
  CONSTRAINT `diet_record_items_chk_1` CHECK (((`food_id` is not null) or (`recipe_id` is not null)))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_record_items`
--

LOCK TABLES `diet_record_items` WRITE;
/*!40000 ALTER TABLE `diet_record_items` DISABLE KEYS */;
INSERT INTO `diet_record_items` VALUES (1,14,1,NULL,50.00,'g',65,'2025-09-20 14:05:59'),(2,14,2,NULL,200.00,'ml',200,'2025-09-20 14:05:59'),(3,14,3,NULL,10.00,'g',17,'2025-09-20 14:05:59'),(4,14,4,NULL,15.00,'g',23,'2025-09-20 14:05:59');
/*!40000 ALTER TABLE `diet_record_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diet_records`
--

DROP TABLE IF EXISTS `diet_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diet_records` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `meal_type` enum('早餐','午餐','晚饭','加餐') NOT NULL,
  `recorded_at` datetime NOT NULL,
  `plan_calories` int(10) unsigned DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_diet_head_user_time` (`user_id`,`recorded_at`),
  CONSTRAINT `fk_diet_head_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diet_records`
--

LOCK TABLES `diet_records` WRITE;
/*!40000 ALTER TABLE `diet_records` DISABLE KEYS */;
INSERT INTO `diet_records` VALUES (1,1,'早餐','2025-09-20 05:11:42',600,'','2025-09-20 05:11:42'),(2,8,'早餐','2025-09-20 05:49:32',600,'','2025-09-20 05:49:32'),(3,8,'早餐','2025-09-20 05:57:08',600,'','2025-09-20 05:57:08'),(5,8,'早餐','2025-09-20 11:50:14',600,'','2025-09-20 11:50:14'),(6,8,'早餐','2025-09-20 11:54:47',600,'','2025-09-20 11:54:47'),(7,8,'早餐','2025-09-20 11:58:56',600,'','2025-09-20 11:58:56'),(8,8,'早餐','2025-09-20 13:16:56',600,'','2025-09-20 13:16:56'),(9,8,'早餐','2025-09-20 21:49:45',600,'使用食谱：燕麦牛奶粥','2025-09-20 13:49:48'),(10,8,'早餐','2025-09-20 21:50:43',600,'使用食谱：鸡胸肉沙拉','2025-09-20 13:50:49'),(11,8,'午餐','2025-09-20 13:51:25',600,'','2025-09-20 13:51:25'),(12,8,'晚饭','2025-09-20 21:57:21',600,'使用食谱：燕麦牛奶粥','2025-09-20 13:57:27'),(13,8,'早餐','2025-09-20 22:02:04',600,'使用食谱：鸡胸肉沙拉','2025-09-20 14:02:07'),(14,8,'晚饭','2025-09-20 22:05:53',600,'使用食谱：燕麦牛奶粥','2025-09-20 14:05:59'),(15,8,'晚饭','2025-10-16 07:30:01',600,'','2025-10-16 07:30:01'),(16,8,'晚饭','2025-10-16 07:31:52',600,'','2025-10-16 07:31:52');
/*!40000 ALTER TABLE `diet_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_records`
--

DROP TABLE IF EXISTS `exercise_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise_records` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `exercise_type` varchar(32) NOT NULL,
  `exercise_name` varchar(64) NOT NULL,
  `duration` int(10) unsigned NOT NULL,
  `calories` int(10) unsigned NOT NULL,
  `exercise_time` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_exercise_user_time` (`user_id`,`exercise_time`),
  CONSTRAINT `fk_exercise_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_records`
--

LOCK TABLES `exercise_records` WRITE;
/*!40000 ALTER TABLE `exercise_records` DISABLE KEYS */;
INSERT INTO `exercise_records` VALUES (1,1,'跑步','跑步',1,1,'2025-09-20 05:10:35','','2025-09-20 05:10:35','2025-09-20 05:10:35'),(2,5,'跑步','跑步',1,1,'2025-09-20 05:16:56','','2025-09-20 05:16:56','2025-09-20 05:16:56'),(3,6,'跑步','跑步',1,1,'2025-09-20 05:17:12','','2025-09-20 05:17:12','2025-09-20 05:17:12'),(4,8,'骑行','骑行',1,300,'2025-09-20 07:44:22','','2025-09-20 05:33:14','2025-09-20 07:44:22'),(5,8,'骑行','骑行',1,1,'2025-09-20 06:09:22','','2025-09-20 05:33:43','2025-09-20 06:09:22'),(7,8,'健身','健身',1,1,'2025-09-20 06:09:26','','2025-09-20 05:46:17','2025-09-20 06:09:26'),(8,8,'游泳','游泳',1,1,'2025-09-20 06:09:32','','2025-09-20 06:00:25','2025-09-20 06:09:32'),(9,8,'游泳','游泳',10,1,'2025-09-20 11:59:10','','2025-09-20 06:05:18','2025-09-20 11:59:10'),(10,8,'跑步','跑步',60,449,'2025-09-20 06:08:39','','2025-09-20 06:08:40','2025-09-20 06:08:40'),(11,8,'跑步','跑步',120,200,'2025-10-16 07:22:56','','2025-10-16 07:22:56','2025-10-16 07:22:56'),(12,8,'跑步','跑步',300,400,'2025-10-16 07:30:19','','2025-10-16 07:30:19','2025-10-16 07:30:19'),(13,8,'瑜伽','瑜伽',66,23,'2025-10-16 07:32:10','','2025-10-16 07:32:10','2025-10-16 07:32:10'),(14,8,'跑步','跑步',111,1111,'2025-10-17 12:59:39','','2025-10-17 12:59:39','2025-10-17 12:59:39');
/*!40000 ALTER TABLE `exercise_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_categories`
--

DROP TABLE IF EXISTS `food_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_categories` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `parent_id` bigint(20) unsigned DEFAULT NULL,
  `sort` int(10) unsigned DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  CONSTRAINT `fk_foodcat_parent` FOREIGN KEY (`parent_id`) REFERENCES `food_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_categories`
--

LOCK TABLES `food_categories` WRITE;
/*!40000 ALTER TABLE `food_categories` DISABLE KEYS */;
INSERT INTO `food_categories` VALUES (1,'主食',NULL,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(2,'肉蛋奶',NULL,2,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(3,'蔬果',NULL,3,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(4,'饮品',NULL,4,'2025-09-20 03:37:44','2025-09-20 03:37:44');
/*!40000 ALTER TABLE `food_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `foods`
--

DROP TABLE IF EXISTS `foods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `foods` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) unsigned DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `alias` varchar(128) DEFAULT NULL,
  `unit` enum('g','ml','份','个','片','杯','勺','其他') DEFAULT 'g',
  `calories_per_100` decimal(8,2) NOT NULL,
  `protein_g_per_100` decimal(8,2) DEFAULT NULL,
  `fat_g_per_100` decimal(8,2) DEFAULT NULL,
  `carbs_g_per_100` decimal(8,2) DEFAULT NULL,
  `sodium_mg_per_100` decimal(8,2) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_food_cat` (`category_id`,`enabled`),
  FULLTEXT KEY `ft_food_name` (`name`,`alias`),
  CONSTRAINT `fk_food_cat` FOREIGN KEY (`category_id`) REFERENCES `food_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `foods`
--

LOCK TABLES `foods` WRITE;
/*!40000 ALTER TABLE `foods` DISABLE KEYS */;
INSERT INTO `foods` VALUES (1,1,'米饭','白米饭','g',116.00,2.60,0.30,25.90,5.00,1,'2025-09-20 03:37:44','2025-09-20 11:15:38'),(2,1,'面条','白面条','g',109.00,4.00,0.40,22.00,5.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(3,2,'鸡胸肉','鸡胸脯肉','g',165.00,31.00,3.60,0.00,74.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(4,2,'鸡蛋','鸡蛋','个',155.00,13.00,11.00,1.10,124.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(5,3,'西兰花','绿花菜','g',33.00,3.00,0.40,6.60,33.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(6,3,'苹果','苹果','g',52.00,0.30,0.20,13.80,1.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(7,4,'牛奶','纯牛奶','ml',42.00,3.00,1.00,5.00,44.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44'),(8,4,'水','纯净水','ml',0.00,0.00,0.00,0.00,0.00,1,'2025-09-20 03:37:44','2025-09-20 03:37:44');
/*!40000 ALTER TABLE `foods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `health_profiles`
--

DROP TABLE IF EXISTS `health_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_profiles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `height` decimal(5,2) DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '当前体重(kg)',
  `bmi` decimal(4,2) DEFAULT NULL COMMENT 'BMI指数',
  `blood_pressure_systolic` int(11) DEFAULT NULL COMMENT '收缩压',
  `blood_pressure_diastolic` int(11) DEFAULT NULL COMMENT '舒张压',
  `blood_sugar` decimal(4,2) DEFAULT NULL COMMENT '血糖值',
  `cholesterol` decimal(4,2) DEFAULT NULL COMMENT '胆固醇值',
  `medical_history` text COMMENT '病史',
  `allergies` text COMMENT '过敏史',
  `medications` text COMMENT '用药情况',
  `emergency_contact` varchar(64) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_health_profile_user` (`user_id`),
  CONSTRAINT `fk_health_profile_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `health_profiles`
--

LOCK TABLES `health_profiles` WRITE;
/*!40000 ALTER TABLE `health_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `health_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(128) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_categories`
--

DROP TABLE IF EXISTS `recipe_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_categories` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `parent_id` bigint(20) unsigned DEFAULT NULL,
  `sort` int(10) unsigned DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_recipe_cat_parent` (`parent_id`),
  CONSTRAINT `fk_recipecat_parent` FOREIGN KEY (`parent_id`) REFERENCES `recipe_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_categories`
--

LOCK TABLES `recipe_categories` WRITE;
/*!40000 ALTER TABLE `recipe_categories` DISABLE KEYS */;
INSERT INTO `recipe_categories` VALUES (1,'早餐',NULL,1,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(2,'午餐',NULL,2,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(3,'晚餐',NULL,3,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(4,'汤品',NULL,4,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(5,'甜品',NULL,5,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(6,'减脂餐',NULL,6,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(7,'增肌餐',NULL,7,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(8,'素食',NULL,8,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(9,'中式早餐',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(10,'西式早餐',1,2,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(11,'热汤',4,1,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(12,'冷汤',4,2,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(13,'低糖甜品',5,1,'2025-09-20 13:27:05','2025-09-20 13:27:05'),(14,'无糖甜品',5,2,'2025-09-20 13:27:05','2025-09-20 13:27:05');
/*!40000 ALTER TABLE `recipe_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipe_items`
--

DROP TABLE IF EXISTS `recipe_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipe_items` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `recipe_id` bigint(20) unsigned NOT NULL,
  `food_id` bigint(20) unsigned NOT NULL,
  `quantity` decimal(10,2) NOT NULL,
  `quantity_unit` varchar(16) NOT NULL,
  `calories` decimal(8,2) DEFAULT NULL COMMENT '热量（千卡）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_recipe_items_recipe` (`recipe_id`),
  KEY `idx_recipe_items_food` (`food_id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipe_items`
--

LOCK TABLES `recipe_items` WRITE;
/*!40000 ALTER TABLE `recipe_items` DISABLE KEYS */;
INSERT INTO `recipe_items` VALUES (5,2,5,150.00,'g',NULL,'2025-09-20 13:29:25'),(6,2,6,100.00,'g',NULL,'2025-09-20 13:29:25'),(7,2,7,50.00,'g',NULL,'2025-09-20 13:29:25'),(8,2,8,30.00,'g',NULL,'2025-09-20 13:29:25'),(9,2,9,10.00,'ml',NULL,'2025-09-20 13:29:25'),(10,3,10,500.00,'g',NULL,'2025-09-20 13:29:25'),(11,3,11,20.00,'g',NULL,'2025-09-20 13:29:25'),(12,3,12,5.00,'ml',NULL,'2025-09-20 13:29:25'),(13,3,13,10.00,'g',NULL,'2025-09-20 13:29:25'),(14,4,14,200.00,'g',NULL,'2025-09-20 13:29:25'),(15,4,15,2.00,'个',NULL,'2025-09-20 13:29:25'),(16,4,16,10.00,'ml',NULL,'2025-09-20 13:29:25'),(17,4,17,500.00,'ml',NULL,'2025-09-20 13:29:25'),(18,5,18,200.00,'g',NULL,'2025-09-20 13:29:25'),(19,5,19,100.00,'g',NULL,'2025-09-20 13:29:25'),(20,5,20,50.00,'g',NULL,'2025-09-20 13:29:25'),(21,5,21,30.00,'g',NULL,'2025-09-20 13:29:25'),(22,6,5,200.00,'g',NULL,'2025-09-20 13:29:25'),(23,6,22,10.00,'ml',NULL,'2025-09-20 13:29:25'),(24,6,23,2.00,'g',NULL,'2025-09-20 13:29:25'),(25,7,24,150.00,'g',NULL,'2025-09-20 13:29:25'),(26,7,25,200.00,'g',NULL,'2025-09-20 13:29:25'),(27,7,16,15.00,'ml',NULL,'2025-09-20 13:29:25'),(28,7,26,10.00,'ml',NULL,'2025-09-20 13:29:25'),(29,8,27,200.00,'g',NULL,'2025-09-20 13:29:25'),(30,8,28,100.00,'g',NULL,'2025-09-20 13:29:25'),(31,8,29,50.00,'g',NULL,'2025-09-20 13:29:25'),(32,8,30,30.00,'g',NULL,'2025-09-20 13:29:25'),(33,8,16,10.00,'ml',NULL,'2025-09-20 13:29:25'),(34,9,31,2.00,'片',NULL,'2025-09-20 13:29:25'),(35,9,15,1.00,'个',NULL,'2025-09-20 13:29:25'),(36,9,32,5.00,'ml',NULL,'2025-09-20 13:29:25'),(37,10,33,120.00,'g',NULL,'2025-09-20 13:29:25'),(38,10,34,80.00,'g',NULL,'2025-09-20 13:29:25'),(39,10,6,50.00,'g',NULL,'2025-09-20 13:29:25'),(40,10,35,15.00,'ml',NULL,'2025-09-20 13:29:25'),(41,11,15,2.00,'个',NULL,'2025-09-20 13:29:25'),(42,11,17,150.00,'ml',NULL,'2025-09-20 13:29:25'),(43,11,36,2.00,'g',NULL,'2025-09-20 13:29:25'),(44,11,37,5.00,'ml',NULL,'2025-09-20 13:29:25'),(45,12,38,300.00,'g',NULL,'2025-09-20 13:29:25'),(46,12,39,200.00,'g',NULL,'2025-09-20 13:29:25'),(47,12,40,10.00,'g',NULL,'2025-09-20 13:29:25'),(48,12,17,800.00,'ml',NULL,'2025-09-20 13:29:25'),(49,13,18,150.00,'g',NULL,'2025-09-20 13:29:25'),(50,13,19,50.00,'g',NULL,'2025-09-20 13:29:25'),(51,13,20,30.00,'g',NULL,'2025-09-20 13:29:25'),(52,13,21,20.00,'g',NULL,'2025-09-20 13:29:25'),(53,14,15,1.00,'个',NULL,'2025-09-20 13:29:25'),(54,14,6,80.00,'g',NULL,'2025-09-20 13:29:25'),(55,14,8,50.00,'g',NULL,'2025-09-20 13:29:25'),(56,14,41,30.00,'g',NULL,'2025-09-20 13:29:25'),(57,15,5,100.00,'g',NULL,'2025-09-20 13:29:25'),(58,15,42,150.00,'g',NULL,'2025-09-20 13:29:25'),(59,15,43,50.00,'g',NULL,'2025-09-20 13:29:25'),(60,15,44,30.00,'g',NULL,'2025-09-20 13:29:25'),(61,15,16,10.00,'ml',NULL,'2025-09-20 13:29:25'),(62,16,45,100.00,'g',NULL,'2025-09-20 13:29:25'),(63,16,46,80.00,'g',NULL,'2025-09-20 13:29:25'),(64,16,47,60.00,'g',NULL,'2025-09-20 13:29:25'),(65,16,48,40.00,'g',NULL,'2025-09-20 13:29:25'),(66,16,16,8.00,'ml',NULL,'2025-09-20 13:29:25'),(109,1,3,1.00,'g',1.65,'2025-10-17 02:11:28'),(110,1,5,1.00,'个',0.33,'2025-10-17 02:11:28');
/*!40000 ALTER TABLE `recipe_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recipes`
--

DROP TABLE IF EXISTS `recipes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recipes` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) unsigned DEFAULT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `created_by` bigint(20) unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `image_url` varchar(255) DEFAULT NULL COMMENT '食谱图片',
  `difficulty_level` enum('简单','中等','困难') DEFAULT '简单' COMMENT '难度等级',
  `cooking_time` int(10) unsigned DEFAULT NULL COMMENT '烹饪时间(分钟)',
  `servings` int(10) unsigned DEFAULT '1' COMMENT '份数',
  PRIMARY KEY (`id`),
  KEY `idx_recipe_cat` (`category_id`,`enabled`),
  CONSTRAINT `fk_recipe_cat` FOREIGN KEY (`category_id`) REFERENCES `recipe_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recipes`
--

LOCK TABLES `recipes` WRITE;
/*!40000 ALTER TABLE `recipes` DISABLE KEYS */;
INSERT INTO `recipes` VALUES (1,1,'燕麦牛奶粥','营养丰富的早餐燕麦粥，富含膳食纤维和蛋白质，适合减肥人群',1,1,'2025-09-20 13:27:05','2025-10-17 02:11:18','/static/recipes/182e52ab-85c2-445a-862e-d82cdd8984db.png','简单',NULL,1),(2,2,'鸡胸肉沙拉','低脂高蛋白的鸡胸肉沙拉，适合减脂期食用，营养均衡',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(3,3,'清蒸鲈鱼','清淡营养的清蒸鲈鱼，富含优质蛋白质，适合晚餐',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(4,4,'番茄鸡蛋汤','简单易做的番茄鸡蛋汤，营养丰富，老少皆宜',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(5,5,'酸奶水果杯','健康低糖的酸奶水果杯，富含维生素和益生菌',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(6,6,'水煮鸡胸肉','减脂期必备的水煮鸡胸肉，低脂高蛋白',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(7,7,'牛肉炒西兰花','高蛋白增肌的牛肉炒西兰花，适合健身人群',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(8,8,'豆腐蔬菜汤','纯素食的豆腐蔬菜汤，营养丰富，适合素食主义者',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(9,1,'全麦吐司配鸡蛋','健康的全麦吐司配水煮蛋，简单营养的早餐选择',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(10,2,'三文鱼牛油果沙拉','富含Omega-3的三文鱼牛油果沙拉，营养丰富',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(11,3,'蒸蛋羹','嫩滑的蒸蛋羹，适合老人和小孩，易消化',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(12,4,'冬瓜排骨汤','清热解暑的冬瓜排骨汤，适合夏季食用',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(13,5,'水果酸奶','新鲜水果配酸奶，健康美味的甜品',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(14,6,'水煮蛋配蔬菜','简单的水煮蛋配蔬菜，低卡路里减脂餐',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(15,7,'鸡胸肉炒饭','高蛋白的鸡胸肉炒饭，适合增肌期食用',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1),(16,8,'素炒时蔬','各种时令蔬菜的素炒，营养均衡的素食选择',1,1,'2025-09-20 13:27:05','2025-09-20 13:27:05',NULL,'简单',NULL,1);
/*!40000 ALTER TABLE `recipes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` bigint(20) unsigned NOT NULL,
  `permission_id` bigint(20) unsigned NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_rp_perm` (`permission_id`),
  CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `openid` varchar(64) NOT NULL,
  `unionid` varchar(64) DEFAULT NULL,
  `nickname` varchar(64) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `gender` enum('男','女','其他') DEFAULT NULL,
  `age` tinyint(3) unsigned DEFAULT NULL,
  `height_cm` decimal(5,2) DEFAULT NULL,
  `weight_kg` decimal(5,2) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `name` varchar(64) DEFAULT NULL COMMENT '用户姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `daily_calorie_goal` int(10) unsigned DEFAULT '600' COMMENT '每日热量目标',
  `daily_exercise_goal` int(10) unsigned DEFAULT '30' COMMENT '每日运动时长目标(分钟)',
  `daily_step_goal` int(10) unsigned DEFAULT '10000' COMMENT '每日步数目标',
  `health_profile_id` bigint(20) unsigned DEFAULT NULL COMMENT '健康档案ID',
  `height` decimal(5,2) DEFAULT NULL COMMENT '身高(cm)',
  `weight` decimal(5,2) DEFAULT NULL COMMENT '体重(kg)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`),
  UNIQUE KEY `uk_users_openid` (`openid`),
  KEY `idx_user_health_profile` (`health_profile_id`),
  CONSTRAINT `fk_user_health_profile` FOREIGN KEY (`health_profile_id`) REFERENCES `health_profiles` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'mock_openid_1758342941103','mock_unionid','用户1103',NULL,NULL,NULL,NULL,NULL,'2025-09-20 04:35:42','2025-09-20 04:35:42',NULL,NULL,600,30,10000,NULL,NULL,NULL),(2,'mock_openid_1758344851909','mock_unionid','用户1909',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:07:32','2025-09-20 05:07:32',NULL,NULL,600,30,10000,NULL,NULL,NULL),(3,'mock_openid_1758345022425','mock_unionid','用户2425',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:10:23','2025-09-20 05:10:23',NULL,NULL,600,30,10000,NULL,NULL,NULL),(4,'mock_openid_1758345049816','mock_unionid','用户9816',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:10:50','2025-09-20 05:10:50',NULL,NULL,600,30,10000,NULL,NULL,NULL),(5,'mock_openid_1758345126019','mock_unionid','用户6019',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:12:06','2025-09-20 05:12:06',NULL,NULL,600,30,10000,NULL,NULL,NULL),(6,'mock_openid_1758345423499','mock_unionid','用户3499',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:17:04','2025-09-20 05:17:04',NULL,NULL,600,30,10000,NULL,NULL,NULL),(7,'mock_openid_1758345440038','mock_unionid','用户0038',NULL,NULL,NULL,NULL,NULL,'2025-09-20 05:17:20','2025-09-20 05:17:20',NULL,NULL,600,30,10000,NULL,NULL,NULL),(8,'oa9iv4npTAZr1t3zOACtGlrTTtxM','ozE0m6ML0sD30OrHh3tynjFa6toA','用户TtxM',NULL,'女',17,NULL,NULL,'2025-09-20 05:33:04','2025-10-17 12:58:27','测试',NULL,600,30,10000,NULL,175.00,80.00);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weight_records`
--

DROP TABLE IF EXISTS `weight_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weight_records` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `weight` decimal(5,2) NOT NULL,
  `recorded_at` datetime NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_weight_user_time` (`user_id`,`recorded_at`),
  CONSTRAINT `fk_weight_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weight_records`
--

LOCK TABLES `weight_records` WRITE;
/*!40000 ALTER TABLE `weight_records` DISABLE KEYS */;
INSERT INTO `weight_records` VALUES (1,8,59.00,'2025-09-20 12:00:00','','2025-09-20 06:57:03'),(2,8,344.00,'2025-09-18 12:00:00','','2025-09-20 06:57:42'),(3,8,34.00,'2025-09-19 12:00:00','','2025-09-20 06:58:35'),(4,8,56.00,'2025-10-20 12:00:00','','2025-09-20 07:41:37'),(5,8,45.00,'2025-09-20 12:00:00','','2025-09-20 11:59:42');
/*!40000 ALTER TABLE `weight_records` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-23  8:47:51
