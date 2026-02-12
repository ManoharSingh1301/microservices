package com.petromanage.dashboard_service.dto;
import java.util.List;

import lombok.Data;


@Data
public class DashboardResponse {
    private CurrentMetricsDto currentMetrics;
    private List<ProductionTrendDto> productionTrends;
    private List<AssetStatusDto> assets;
    private List<MaintenancePredictionDto> maintenancePredictions;
    private List<RecentReportDto> recentReports;
}