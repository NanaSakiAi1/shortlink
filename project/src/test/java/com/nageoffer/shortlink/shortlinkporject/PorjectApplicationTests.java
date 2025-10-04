package com.nageoffer.shortlink.shortlinkporject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PorjectApplicationTests {
    public static final String SQL = "CREATE TABLE `t_link_%d` (\n" +
            "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "  `domain` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '域名',\n" +
            "  `short_uri` varchar(8) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '短连接',\n" +
            "  `full_short_url` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '完整短连接',\n" +
            "  `origin_url` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '原始链接',\n" +
            "  `click_num` int NOT NULL DEFAULT '0' COMMENT '点击量',\n" +
            "  `gid` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL DEFAULT 'default' COMMENT '分组标识',\n" +
            "  `favicon` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL COMMENT '网站图标',\n" +
            "  `enable_status` tinyint(1) NOT NULL COMMENT '启用标识 0:启用\\n1:未启用',\n" +
            "  `created_type` tinyint(1) NOT NULL COMMENT '创建类型',\n" +
            "  `valid_date_type` tinyint(1) NOT NULL COMMENT '有效期类型',\n" +
            "  `valid_date` datetime NOT NULL COMMENT '有效期',\n" +
            "  `describe` varchar(1024) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '描述',\n" +
            "  `create_time` datetime NOT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime NOT NULL COMMENT '修改时间',\n" +
            "  `del_flag` tinyint(1) NOT NULL COMMENT '删除标识 0：未删除 1：已删除',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }

    @Test
    void contextLoads() {
    }

}
