/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50730
Source Host           : localhost:3306
Source Database       : labtest

Target Server Type    : MYSQL
Target Server Version : 50730
File Encoding         : 65001

Date: 2022-03-20 19:55:04
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for errorlog
-- ----------------------------
DROP TABLE IF EXISTS `errorlog`;
CREATE TABLE `errorlog` (
  `id` bigint(100) NOT NULL,
  `call_name` varchar(255) DEFAULT NULL,
  `call_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `call_function_full_name` varchar(255) DEFAULT NULL,
  `exception_message` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of errorlog
-- ----------------------------
