package com.venuex.host_mngt_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HostMngtServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HostMngtServiceApplication.class, args);
	}

}
