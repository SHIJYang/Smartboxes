CREATE TABLE `t_box_switch_log` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`user_id` bigint NOT NULL COMMENT '用户ID',
	`old_main_box_id` bigint NOT NULL COMMENT '旧主盒ID',
	`new_main_box_id` bigint NOT NULL COMMENT '新主盒ID',
	`switch_type` tinyint NOT NULL COMMENT '1-手动切换，2-自动切换',
	`switch_reason` varchar(255) NOT NULL COMMENT '切换原因',
	`switch_time` datetime NOT NULL COMMENT '切换时间',
	`data_sync_status` tinyint NOT NULL COMMENT '0-未同步，1-同步成功，2-同步失败',
	PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '盒子切换日志表';