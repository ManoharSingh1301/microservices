package com.petromanage.dashboard_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petromanage.dashboard_service.dto.DashboardResponse;
import com.petromanage.dashboard_service.service.DashboardService;


@RestController
@RequestMapping("/api/dashboard")
// // @CrossOrigin is handled by the API Gateway, but useful for local testing
// @CrossOrigin(origins = "http://localhost:5173") 
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/analytics")
    public DashboardResponse getDashboardAnalytics() {
        return dashboardService.generateDashboardData();
    }
}