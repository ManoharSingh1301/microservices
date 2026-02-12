package com.petromanage.dashboard_service.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Matches "PRODUCTION-SERVICE" from your Eureka logs
@FeignClient(name = "PRODUCTION-SERVICE")
public interface ProductionClient {
    @GetMapping("/api/production/records")
    List<Map<String, Object>> getProductionRecords();
}
