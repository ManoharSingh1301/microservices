package com.petromanage.dashboard_service.service;

import com.petromanage.dashboard_service.client.*;
import com.petromanage.dashboard_service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired private AssetClient assetClient;
    @Autowired private MaintenanceClient maintenanceClient;
    @Autowired private ProductionClient productionClient;
    @Autowired private ComplianceClient complianceClient;

    public DashboardResponse generateDashboardData() {
        DashboardResponse response = new DashboardResponse();
        
        // 1. Fetch Data Safely 
        // We use empty lists as default so the dashboard doesn't crash if one service is down
        List<AssetStatusDto> assets = new ArrayList<>();
        List<Map<String, Object>> workOrders = new ArrayList<>();
        List<Map<String, Object>> productionRecords = new ArrayList<>();
        List<RecentReportDto> reports = new ArrayList<>();

        // FETCH ASSETS
        try { 
            assets = assetClient.getAllAssets(); 
        } catch (Exception e) { 
            System.err.println("⚠️ ASSET-SERVICE is down or empty: " + e.getMessage()); 
        }

        // FETCH MAINTENANCE
        try { 
            workOrders = maintenanceClient.getAllWorkOrders(); 
        } catch (Exception e) { 
            System.err.println("⚠️ MAINTENANCE-SERVICE is down or empty: " + e.getMessage()); 
        }

        // FETCH PRODUCTION
        try { 
            productionRecords = productionClient.getProductionRecords(); 
        } catch (Exception e) { 
            System.err.println("⚠️ PRODUCTION-SERVICE is down or empty: " + e.getMessage()); 
        }

        // FETCH COMPLIANCE
        try { 
            reports = complianceClient.getAllReports(); 
        } catch (Exception e) { 
            System.err.println("⚠️ COMPLIANCE-SERVICE is down or empty: " + e.getMessage()); 
        }

        // 2. Calculate Metrics from Real Data
        CurrentMetricsDto metrics = new CurrentMetricsDto();

        // A. Asset Utilization
        long totalAssets = assets.size();
        long activeAssets = assets.stream().filter(a -> "OPERATIONAL".equalsIgnoreCase(a.getStatus())).count();
        double util = totalAssets > 0 ? ((double)activeAssets / totalAssets) * 100 : 0;
        metrics.setAssetUtilization(Math.round(util * 10.0) / 10.0);
        metrics.setUtilizationChange(0.0); 

        // B. Production Efficiency (Mock calculation on real data count)
        // If we have production records, assume 88% efficiency, else 0%
        metrics.setProductionEfficiency(productionRecords.isEmpty() ? 0 : 88.5); 
        metrics.setEfficiencyChange(1.2);

        // C. Maintenance Due (Count work orders NOT completed)
        long dueCount = workOrders.stream().filter(w -> {
            String status = (String) w.get("status");
            return status != null && !status.equalsIgnoreCase("COMPLETED");
        }).count();
        metrics.setMaintenanceDue((int) dueCount);

        // D. Total Downtime (Placeholder logic)
        metrics.setTotalDowntime(12.5); 
        metrics.setDowntimeChange(-2.0);

        response.setCurrentMetrics(metrics);

        // 3. Set Lists for Tables
        response.setAssets(assets);
        response.setRecentReports(reports.stream().limit(5).collect(Collectors.toList()));

        // 4. Mock Trends for Charts (Hard to calculate without historical DB)
        List<ProductionTrendDto> trends = new ArrayList<>();
        trends.add(new ProductionTrendDto("2024-02-10", 85, 2));
        trends.add(new ProductionTrendDto("2024-02-11", 89, 1));
        trends.add(new ProductionTrendDto("2024-02-12", 92, 0));
        response.setProductionTrends(trends);

        // 5. Predictions (Mock based on real asset)
        List<MaintenancePredictionDto> predictions = new ArrayList<>();
        if (!assets.isEmpty()) {
            predictions.add(new MaintenancePredictionDto(assets.get(0).getId(), assets.get(0).getName(), "2024-02-28", "high", 85));
        }
        response.setMaintenancePredictions(predictions);

        return response;
    }
}