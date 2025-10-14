package com.nageoffer.shortlink.aggregation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
		"com.nageoffer.shortlink.aggregation",
		"com.nageoffer.shortlink.shortlinkporject",
		"com.nageoffer.shortlink.admin"
})
@EnableDiscoveryClient
@MapperScan(value = {
		"com.nageoffer.shortlink.shortlinkporject.dao.mapper",
		"com.nageoffer.shortlink.admin.dao.mapper"
})

public class AggregationApplication {

	public static void main(String[] args) {
		SpringApplication.run(AggregationApplication.class, args);
	}

}
