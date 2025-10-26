CREATE TABLE `t_emotion` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
	`item_id` bigint NOT NULL COMMENT '物品ID',
	`emotion_tag` tinyint NOT NULL COMMENT '1-开心，2-怀念，3-重要，4-其他',
	`emotion_remark` text COMMENT '情感备注',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	`update_time` datetime NOT NULL COMMENT '更新时间',
	PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '情感表';