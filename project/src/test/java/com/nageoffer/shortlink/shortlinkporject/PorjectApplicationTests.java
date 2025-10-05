package com.nageoffer.shortlink.shortlinkporject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PorjectApplicationTests {
    public static final String SQL = "CREATE TABLE `t_link_goto_%d` (\n" +
            "  `id` int NOT NULL,\n" +
            "  `gid` varchar(32) COLLATE utf8mb4_general_ci NOT NULL,\n" +
            "  `full_short_rul` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,\n" +
            "  PRIMARY KEY (`id`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }

    @Test
    void contextLoads() {
    }

}
