package com.petromanage.dashboard_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.petromanage.dashboard_service.dto.AssetStatusDto;

// Matches "ASSETS-SERVICE" from your Eureka logs
@FeignClient(name = "ASSETS-SERVICE") 
public interface AssetClient {
    @GetMapping("/api/assets") 
    List<AssetStatusDto> getAllAssets();
}
