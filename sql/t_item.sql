CREATE TABLE `t_item` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
	`item_code` varchar(255) NOT NULL COMMENT '商品编码',
	`box_id` bigint NOT NULL COMMENT '盒子ID',
	`auto_recognize_name` varchar(255) COMMENT '自动识别名称',
	`manual_edit_name` varchar(255) COMMENT '手动编辑名称',
	`item_tag` varchar(255) COMMENT '商品标签',
	`item_desc` text COMMENT '商品描述',
	`put_in_time` datetime NOT NULL COMMENT '放入时间',
	`expire_time` datetime COMMENT '过期时间',
	`is_valid` tinyint NOT NULL COMMENT '0-已取出，1-在盒内',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	`update_time` datetime NOT NULL COMMENT '更新时间'
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '商品表';