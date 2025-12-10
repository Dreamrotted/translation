/*
 Navicat Premium Dump SQL

 Source Server         : mysql8.0
 Source Server Type    : MySQL
 Source Server Version : 80026 (8.0.26)
 Source Host           : localhost:3306
 Source Schema         : room

 Target Server Type    : MySQL
 Target Server Version : 80026 (8.0.26)
 File Encoding         : 65001

 Date: 20/11/2025 21:14:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin');

-- ----------------------------
-- Table structure for blacklist
-- ----------------------------
DROP TABLE IF EXISTS `blacklist`;
CREATE TABLE `blacklist`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` int NOT NULL COMMENT '学生ID',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加入黑名单原因',
  `start_time` datetime NOT NULL COMMENT '黑名单开始时间',
  `end_time` datetime NOT NULL COMMENT '黑名单结束时间',
  `add_type` tinyint NOT NULL DEFAULT 0 COMMENT '加入方式（0系统自动 1管理员手动）',
  `admin_id` int NULL DEFAULT NULL COMMENT '操作管理员ID（手动加入时）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0已解除 1生效中）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '黑名单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of blacklist
-- ----------------------------
INSERT INTO `blacklist` VALUES (1, 11, '未按时签到', '2025-11-20 20:18:41', '2025-11-27 20:18:41', 1, 1, 0);

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` int NOT NULL COMMENT '学生ID',
  `type` tinyint NOT NULL COMMENT '类型（0投诉 1评价）',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `room_id` int NULL DEFAULT NULL COMMENT '关联自习室ID',
  `seat_id` int NULL DEFAULT NULL COMMENT '关联座位ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0待处理 1已回复 2已关闭）',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '管理员回复',
  `reply_admin_id` int NULL DEFAULT NULL COMMENT '回复管理员ID',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '投诉与评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------

-- ----------------------------
-- Table structure for reservations
-- ----------------------------
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` int NOT NULL COMMENT '学生id',
  `room_id` int NOT NULL COMMENT '自习室id',
  `seat_id` int NULL DEFAULT NULL COMMENT '座位id',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '已预约' COMMENT '预约状态（如已预约、已取消、已过期等）',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '签到时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '签退时间',
  `is_checked_in` tinyint NOT NULL DEFAULT 0 COMMENT '是否已签到（0未签到 1已签到）',
  `is_checked_out` tinyint NOT NULL DEFAULT 0 COMMENT '是否已签退（0未签退 1已签退）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_seat_id`(`seat_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reservations
-- ----------------------------
INSERT INTO `reservations` VALUES (7, 7, 1, 1, '2025-03-01 09:30:00', '2025-03-01 13:15:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (8, 9, 1, 2, '2025-03-03 09:30:00', '2025-03-03 13:15:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (10, 9, 2, 4, '2025-03-04 09:30:00', '2025-03-04 10:15:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (11, 9, 4, NULL, '2025-03-05 09:30:00', '2025-03-05 12:30:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (12, 9, 2, 5, '2025-03-11 09:00:00', '2025-03-11 11:15:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (13, 9, 2, 4, '2025-03-13 09:00:00', '2025-03-13 12:45:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (14, 9, 1, 1, '2025-03-13 09:00:00', '2025-03-13 11:15:00', '已过期', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (15, 4, 1, 1, '2025-11-21 09:00:00', '2025-11-21 11:30:00', '已预约', NULL, NULL, 0, 0);
INSERT INTO `reservations` VALUES (16, 11, 1, 6, '2025-11-21 07:00:00', '2025-11-21 11:00:00', '已预约', NULL, NULL, 0, 0);

-- ----------------------------
-- Table structure for seat
-- ----------------------------
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `room_id` int NOT NULL COMMENT '自习室ID',
  `seat_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '座位编号',
  `seat_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '普通座位' COMMENT '座位类型（如普通座位、插座座位、静音区等）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0可用 1维修中 2禁用）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_room_id`(`room_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 47 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '座位表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seat
-- ----------------------------
INSERT INTO `seat` VALUES (1, 1, 'A001', '普通座位', 1);
INSERT INTO `seat` VALUES (2, 1, 'A002', '插座座位', 0);
INSERT INTO `seat` VALUES (3, 1, 'A003', '静音区', 0);
INSERT INTO `seat` VALUES (4, 2, 'B001', '普通座位', 0);
INSERT INTO `seat` VALUES (5, 2, 'B002', '插座座位', 0);
INSERT INTO `seat` VALUES (6, 1, 'A001', '普通座位', 0);
INSERT INTO `seat` VALUES (7, 1, 'A002', '普通座位', 0);
INSERT INTO `seat` VALUES (8, 1, 'A003', '普通座位', 0);
INSERT INTO `seat` VALUES (9, 1, 'A004', '普通座位', 0);
INSERT INTO `seat` VALUES (10, 1, 'A005', '普通座位', 0);
INSERT INTO `seat` VALUES (11, 1, 'A006', '普通座位', 0);
INSERT INTO `seat` VALUES (12, 1, 'A007', '普通座位', 0);
INSERT INTO `seat` VALUES (13, 1, 'A008', '普通座位', 0);
INSERT INTO `seat` VALUES (14, 1, 'A009', '普通座位', 0);
INSERT INTO `seat` VALUES (15, 1, 'A010', '普通座位', 0);
INSERT INTO `seat` VALUES (16, 1, 'B001', '插座座位', 0);
INSERT INTO `seat` VALUES (17, 1, 'B002', '插座座位', 0);
INSERT INTO `seat` VALUES (18, 1, 'B003', '插座座位', 0);
INSERT INTO `seat` VALUES (19, 1, 'B004', '插座座位', 0);
INSERT INTO `seat` VALUES (20, 1, 'B005', '插座座位', 0);
INSERT INTO `seat` VALUES (21, 1, 'B006', '插座座位', 0);
INSERT INTO `seat` VALUES (22, 1, 'B007', '插座座位', 0);
INSERT INTO `seat` VALUES (23, 1, 'B008', '插座座位', 0);
INSERT INTO `seat` VALUES (24, 1, 'B009', '插座座位', 0);
INSERT INTO `seat` VALUES (25, 1, 'B010', '插座座位', 0);
INSERT INTO `seat` VALUES (26, 1, 'C001', '静音区', 0);
INSERT INTO `seat` VALUES (27, 1, 'C002', '静音区', 0);
INSERT INTO `seat` VALUES (28, 1, 'C003', '静音区', 0);
INSERT INTO `seat` VALUES (29, 1, 'C004', '静音区', 0);
INSERT INTO `seat` VALUES (30, 1, 'C005', '静音区', 0);
INSERT INTO `seat` VALUES (31, 1, 'C006', '静音区', 0);
INSERT INTO `seat` VALUES (32, 1, 'C007', '静音区', 0);
INSERT INTO `seat` VALUES (33, 1, 'C008', '静音区', 0);
INSERT INTO `seat` VALUES (34, 1, 'C009', '静音区', 0);
INSERT INTO `seat` VALUES (35, 1, 'C010', '静音区', 0);
INSERT INTO `seat` VALUES (36, 1, 'D', '讨论区', 0);
INSERT INTO `seat` VALUES (37, 1, 'D001', '讨论区', 0);
INSERT INTO `seat` VALUES (38, 1, 'D002', '讨论区', 0);
INSERT INTO `seat` VALUES (39, 1, 'D003', '讨论区', 0);
INSERT INTO `seat` VALUES (40, 1, 'D004', '讨论区', 0);
INSERT INTO `seat` VALUES (41, 1, 'D005', '讨论区', 0);
INSERT INTO `seat` VALUES (42, 1, 'D006', '讨论区', 0);
INSERT INTO `seat` VALUES (43, 1, 'D007', '讨论区', 0);
INSERT INTO `seat` VALUES (44, 1, 'D008', '讨论区', 0);
INSERT INTO `seat` VALUES (45, 1, 'D009', '讨论区', 0);
INSERT INTO `seat` VALUES (46, 1, 'D010', '讨论区', 0);

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_number` int NOT NULL COMMENT '学号',
  `student_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `gender` tinyint NOT NULL COMMENT '性别(1男 0女)',
  `major_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '专业',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (4, 2023004, '王柳', 0, '100', 'c1716dad7987c19548f5f72818a05f21');
INSERT INTO `student` VALUES (5, 2023003, '何骑', 1, '100', 'c1716dad7987c19548f5f72818a05f21');
INSERT INTO `student` VALUES (6, 2023005, '张尚武', 1, '100', 'c1716dad7987c19548f5f72818a05f21');
INSERT INTO `student` VALUES (7, 2023100, '刘飒', 1, '100', 'c1716dad7987c19548f5f72818a05f21');
INSERT INTO `student` VALUES (9, 2025001, '测试修改', 1, '计算机科学与技术', 'c1716dad7987c19548f5f72818a05f21');
INSERT INTO `student` VALUES (11, 2025002, '吴邪', 1, '计算机科学与技术', 'c1716dad7987c19548f5f72818a05f21');

-- ----------------------------
-- Table structure for study_rooms
-- ----------------------------
DROP TABLE IF EXISTS `study_rooms`;
CREATE TABLE `study_rooms`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `room_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '自习室编号',
  `capacity` int NOT NULL COMMENT '自习室容量',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '自习室位置',
  `residual_capacity` int NULL DEFAULT NULL COMMENT '自习室剩余容量',
  `status` tinyint NOT NULL COMMENT '是否禁用（1是0否）',
  `opening_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开放开始时间',
  `closing_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开放结束时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `room_number`(`room_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of study_rooms
-- ----------------------------
INSERT INTO `study_rooms` VALUES (1, 'A101', 30, '教学楼 A 座 1 层', 30, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (2, 'A102', 35, '教学楼 A 座 1 层', 35, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (3, 'A201', 40, '教学楼 A 座 2 层', 40, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (4, 'A202', 45, '教学楼 A 座 2 层', 45, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (5, 'B101', 25, '教学楼 B 座 1 层', 25, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (6, 'B102', 30, '教学楼 B 座 1 层', 30, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (7, 'B201', 35, '教学楼 B 座 2 层', 35, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (8, 'B202', 40, '教学楼 B 座 2 层', 40, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (9, 'C101', 20, '教学楼 C 座 1 层', 20, 1, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (10, 'C102', 25, '教学楼 C 座 1 层', 25, 0, '08:00', '22:00');
INSERT INTO `study_rooms` VALUES (16, 'C103', 30, '教学楼C座一层', NULL, 0, '08:00', '18:30');

-- ----------------------------
-- Table structure for violation_record
-- ----------------------------
DROP TABLE IF EXISTS `violation_record`;
CREATE TABLE `violation_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_id` int NOT NULL COMMENT '学生ID',
  `reservation_id` int NULL DEFAULT NULL COMMENT '预约记录ID（系统自动记录时关联）',
  `violation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '违规类型（未按时签到、未主动签退、其他）',
  `violation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '违规描述',
  `violation_time` datetime NOT NULL COMMENT '违规时间',
  `record_type` tinyint NOT NULL DEFAULT 0 COMMENT '记录类型（0系统自动 1管理员手动）',
  `admin_id` int NULL DEFAULT NULL COMMENT '记录管理员ID（手动记录时）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '处理状态（0未处理 1已处理）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '违规记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of violation_record
-- ----------------------------
INSERT INTO `violation_record` VALUES (1, 11, NULL, '未按时签到', '未按时签到', '2025-11-20 20:16:03', 1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;
