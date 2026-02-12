// package com.petromanage.dashboard_service;



// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// import org.springframework.cloud.openfeign.EnableFeignClients;

// @SpringBootApplication
// @EnableDiscoveryClient
// @EnableFeignClients
// public class DashboardServiceApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(DashboardServiceApplication.class, args);
// 	}

// }

package com.petromanage.dashboard_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <--- ADD THIS

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // <--- IMPORTANT
public class DashboardServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(DashboardServiceApplication.class, args);
	}
}
