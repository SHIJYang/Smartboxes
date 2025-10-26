CREATE TABLE `t_box` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`box_code` varchar(255) NOT NULL COMMENT '盒子编码',
	`user_id` bigint NOT NULL COMMENT '关联重构后t_user表的用户ID',
	`box_name` varchar(255) NOT NULL COMMENT '盒子名称',
	`box_type` tinyint NOT NULL COMMENT '0-子盒，1-主盒',
	`status` tinyint NOT NULL COMMENT '0-离线，1-在线，2-低电量，3-故障',
	`rssi` int DEFAULT NULL COMMENT '信号强度',
	`battery` int DEFAULT NULL COMMENT '0-100百分比',
	`network_delay` int DEFAULT NULL COMMENT '单位ms，仅主盒记录',
	`last_heartbeat_time` datetime NOT NULL COMMENT '最后心跳时间',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	`update_time` datetime NOT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT '盒子信息表';