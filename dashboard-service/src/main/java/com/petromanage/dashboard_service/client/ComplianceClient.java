package com.petromanage.dashboard_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.petromanage.dashboard_service.dto.RecentReportDto;

// Matches "COMPLIANCE-SERVICE" from your Eureka logs
@FeignClient(name = "COMPLIANCE-SERVICE")
public interface ComplianceClient {
    @GetMapping("/api/compliance/reports")
    List<RecentReportDto> getAllReports();
}
