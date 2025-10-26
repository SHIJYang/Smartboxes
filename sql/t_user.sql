CREATE TABLE `t_user` (
	`id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
	`user_account` varchar(255) NOT NULL COMMENT '普通用户登录账号',
	`user_password` varchar(255) NOT NULL COMMENT '登录密码（加密存储）',
	`username` varchar(255) COMMENT '用户昵称',
	`phone` varchar(255) COMMENT '可选，用于密码找回',
	`create_time` datetime NOT NULL COMMENT '账号注册时间',
	`update_time` datetime NOT NULL COMMENT '信息更新时间',
	PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARSET = utf8mb4 COMMENT '普通用户表（账号密码登录）';