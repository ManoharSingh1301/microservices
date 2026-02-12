package com.petromanage.dashboard_service.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Matches "MAINTENANCE-SERVICE" from your Eureka logs
@FeignClient(name = "MAINTENANCE-SERVICE")
public interface MaintenanceClient {
    @GetMapping("/api/maintenance/work-orders")
    List<Map<String, Object>> getAllWorkOrders();
}
