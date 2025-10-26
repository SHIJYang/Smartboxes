CREATE TABLE `t_item_operate_log` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`item_id` bigint NOT NULL COMMENT '商品ID',
	`operate_type` tinyint NOT NULL COMMENT '1-放入，2-取出，3-手动编辑，4-自动识别更新',
	`operate_user_id` bigint NOT NULL COMMENT '操作用户ID',
	`operate_content` varchar(255) COMMENT '操作内容',
	`operate_time` datetime NOT NULL COMMENT '操作时间',
	PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '商品操作日志表';