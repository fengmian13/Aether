/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80033
 Source Host           : localhost:3306
 Source Schema         : anther

 Target Server Type    : MySQL
 Target Server Version : 80033
 File Encoding         : 65001

 Date: 30/12/2025 14:41:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '会话ID',
  `session_type` int NOT NULL COMMENT '1=私聊, 2=群聊, 3=会议聊天',
  `send_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '发送人ID',
  `msg_type` int NULL DEFAULT NULL COMMENT '消息类型：1=文本, 2=图片, 3=视频, 4=文件, 5=撤回指令, 6=系统通知',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '消息详情',
  `send_time` bigint NULL DEFAULT NULL COMMENT '发送时间',
  `receive_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '接收人ID',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_suffix` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '文件后缀',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0:正在发送 1:已发送',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_info
-- ----------------------------
DROP TABLE IF EXISTS `group_info`;
CREATE TABLE `group_info`  (
  `group_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL,
  `group_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '群组名',
  `group_Introducte` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL COMMENT '群组描述',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_contact
-- ----------------------------
DROP TABLE IF EXISTS `user_contact`;
CREATE TABLE `user_contact`  (
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `contact_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '联系人ID',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0:待处理 1:好友 2:已删除好友 3:已拉黑好友',
  `last_update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`user_id`, `contact_id`) USING BTREE,
  INDEX `idx_contact_id`(`contact_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系人' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_contact_apply
-- ----------------------------
DROP TABLE IF EXISTS `user_contact_apply`;
CREATE TABLE `user_contact_apply`  (
  `apply_id` int NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `apply_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请人id',
  `receive_user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '接收人ID',
  `last_apply_time` bigint NULL DEFAULT NULL COMMENT '最后申请时间',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态0:待处理 1:已同意  2:已拒绝 3:已拉黑',
  PRIMARY KEY (`apply_id`) USING BTREE,
  UNIQUE INDEX `idx_key`(`apply_user_id` ASC, `receive_user_id` ASC) USING BTREE,
  INDEX `idx_last_apply_time`(`last_apply_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 136938 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系人申请' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group`  (
  `user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL,
  `group_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `user_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `join_type` tinyint(1) NULL DEFAULT NULL COMMENT '0:直接加入  1:同意后加好友',
  `sex` tinyint(1) NULL DEFAULT NULL COMMENT '性别 0:女 1:男',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `last_login_time` bigint NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_off_time` bigint NULL DEFAULT NULL COMMENT '最后离开时间',
  `meeting_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人会议号',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `idx_key_email`(`email` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
