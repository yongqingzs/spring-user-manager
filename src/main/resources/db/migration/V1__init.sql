CREATE TABLE if not exists `user` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` varchar(32) NOT NULL DEFAULT '' COMMENT '登录用户名，唯一',
    `realname` varchar(32) NOT NULL DEFAULT '' COMMENT '中文名',
    `password` varchar(256) NOT NULL DEFAULT '' COMMENT '登录密码，加密后存储',
    `salt` varchar(128) NOT NULL DEFAULT '' COMMENT '密码加盐',
    `mobile` varchar(64) NOT NULL DEFAULT '' COMMENT '手机号码',
    `idno` varchar(128) NOT NULL DEFAULT '' COMMENT '身份证号码',
    `sex` tinyint NOT NULL DEFAULT 1 COMMENT '用户性别：1男；2女',
    `email` varchar(128) NOT NULL DEFAULT '' COMMENT '用户邮箱',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户状态：1启用,2禁用',
    `user_ext` varchar(512) DEFAULT NULL COMMENT '用户相关扩展字段',
    `creator` varchar(32) DEFAULT NULL COMMENT '创建人',
    `modifier` varchar(32) DEFAULT NULL COMMENT '更新人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '用户表';

CREATE TABLE if not exists `department` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `code` VARCHAR(32) NOT NULL COMMENT '部门编号，唯一',
    `parent_code` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '父部门编号',
    `name` varchar(32) NOT NULL DEFAULT '' COMMENT '部门名称',
    `description` varchar(256) NOT NULL DEFAULT '' COMMENT '描述信息',
    `creator` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
    `modifier` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '部门表';

CREATE TABLE if not exists `user_department` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `username` varchar(32) NOT NULL comment '用户',
    `department_code` VARCHAR(32) NOT NULL COMMENT '部门id',
    `creator` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COMMENT = '用户部门表';