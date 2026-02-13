package com.petromanage.dashboard_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.petromanage.dashboard_service.client.AssetClient;
import com.petromanage.dashboard_service.client.ComplianceClient;
import com.petromanage.dashboard_service.client.MaintenanceClient;
import com.petromanage.dashboard_service.client.ProductionClient;
import com.petromanage.dashboard_service.dto.AssetStatusDto;
import com.petromanage.dashboard_service.dto.CurrentMetricsDto;
import com.petromanage.dashboard_service.dto.DashboardResponse;
import com.petromanage.dashboard_service.dto.MaintenancePredictionDto;
import com.petromanage.dashboard_service.dto.ProductionTrendDto;
import com.petromanage.dashboard_service.dto.RecentReportDto;

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
        long activeAssets = assets.stream().filter(a -> {
            String status = a.getStatus();
            return status != null && (
                status.equalsIgnoreCase("OPERATIONAL") || 
                status.equalsIgnoreCase("ACTIVE")
            );
        }).count();
        double util = totalAssets > 0 ? ((double)activeAssets / totalAssets) * 100 : 0;
        metrics.setAssetUtilization(Math.round(util * 10.0) / 10.0);
        metrics.setUtilizationChange(0.0); 

        // B. Production Efficiency (Real calculation from production records)
        double productionEfficiency = 0.0;
        System.out.println("🔍 DEBUG: Production records count: " + productionRecords.size());
        
        if (!productionRecords.isEmpty()) {
            double totalActual = 0.0;
            double totalPlanned = 0.0;
            
            for (Map<String, Object> record : productionRecords) {
                System.out.println("🔍 DEBUG: Production record: " + record);
                
                // Get actual volume from record
                Object actualVolumeObj = record.get("actualVolume");
                if (actualVolumeObj != null) {
                    double actualVolume = actualVolumeObj instanceof Double ? 
                        (Double) actualVolumeObj : 
                        Double.parseDouble(actualVolumeObj.toString());
                    totalActual += actualVolume;
                    System.out.println("✅ DEBUG: Added actual volume: " + actualVolume + ", Total actual: " + totalActual);
                }
                
                // Get planned volume from dailyPlannedTarget field
                Object plannedTargetObj = record.get("dailyPlannedTarget");
                System.out.println("🔍 DEBUG: dailyPlannedTarget object: " + plannedTargetObj);
                
                if (plannedTargetObj != null) {
                    double plannedVolume = plannedTargetObj instanceof Double ? 
                        (Double) plannedTargetObj : 
                        Double.parseDouble(plannedTargetObj.toString());
                    totalPlanned += plannedVolume;
                    System.out.println("✅ DEBUG: Added planned volume: " + plannedVolume + ", Total planned: " + totalPlanned);
                } else {
                    System.out.println("⚠️ DEBUG: dailyPlannedTarget is null");
                }
            }
            
            System.out.println("📊 DEBUG: Total Actual: " + totalActual + ", Total Planned: " + totalPlanned);
            
            // Calculate overall efficiency
            if (totalPlanned > 0) {
                productionEfficiency = (totalActual / totalPlanned) * 100;
                productionEfficiency = Math.round(productionEfficiency * 10.0) / 10.0; // Round to 1 decimal
                System.out.println("✅ DEBUG: Production Efficiency: " + productionEfficiency + "%");
            } else {
                System.out.println("⚠️ DEBUG: Total planned is 0, cannot calculate efficiency");
            }
        } else {
            System.out.println("⚠️ DEBUG: No production records found");
        }
        
        metrics.setProductionEfficiency(productionEfficiency); 
        metrics.setEfficiencyChange(0.0); // TODO: Calculate change from previous period

        // Calculate individual asset utilization from production records
        if (!assets.isEmpty() && !productionRecords.isEmpty()) {
            for (AssetStatusDto asset : assets) {
                // Find production records for this asset
                double assetActual = 0.0;
                double assetPlanned = 0.0;
                
                for (Map<String, Object> record : productionRecords) {
                    Object assetIdObj = record.get("assetId");
                    if (assetIdObj != null) {
                        Long recordAssetId = assetIdObj instanceof Long ? 
                            (Long) assetIdObj : 
                            Long.parseLong(assetIdObj.toString());
                        
                        if (recordAssetId.equals(asset.getAssetId())) {
                            // Sum actual volume for this asset
                            Object actualVolumeObj = record.get("actualVolume");
                            if (actualVolumeObj != null) {
                                double actualVolume = actualVolumeObj instanceof Double ? 
                                    (Double) actualVolumeObj : 
                                    Double.parseDouble(actualVolumeObj.toString());
                                assetActual += actualVolume;
                            }
                            
                            // Sum planned volume for this asset
                            Object plannedTargetObj = record.get("dailyPlannedTarget");
                            if (plannedTargetObj != null) {
                                double plannedVolume = plannedTargetObj instanceof Double ? 
                                    (Double) plannedTargetObj : 
                                    Double.parseDouble(plannedTargetObj.toString());
                                assetPlanned += plannedVolume;
                            }
                        }
                    }
                }
                
                // Calculate utilization for this asset
                int assetUtilization = 0;
                if (assetPlanned > 0) {
                    double assetUtil = (assetActual / assetPlanned) * 100;
                    // Cap at 100% for individual assets (realistic display)
                    assetUtilization = (int) Math.min(100, Math.round(assetUtil));
                }
                asset.setUtilization(assetUtilization);
                System.out.println("✅ DEBUG: Asset " + asset.getName() + " utilization: " + assetUtilization + "%");
            }
        }

        // C. Maintenance Due (Count work orders NOT completed)
        long dueCount = workOrders.stream().filter(w -> {
            String status = (String) w.get("status");
            return status != null && !status.equalsIgnoreCase("COMPLETED");
        }).count();
        metrics.setMaintenanceDue((int) dueCount);

        response.setCurrentMetrics(metrics);

        // 3. Set Lists for Tables
        response.setAssets(assets);
        response.setRecentReports(reports.stream().limit(5).collect(Collectors.toList()));

        // 4. Production Trends from Real Production Records
        List<ProductionTrendDto> trends = new ArrayList<>();
        
        if (!productionRecords.isEmpty()) {
            // Group production records by date
            Map<String, Double> actualByDate = new HashMap<>();
            Map<String, Double> plannedByDate = new HashMap<>();
            
            for (Map<String, Object> record : productionRecords) {
                Object dateObj = record.get("date");
                if (dateObj != null) {
                    String dateStr = dateObj.toString();
                    
                    // Get actual volume
                    Object actualVolumeObj = record.get("actualVolume");
                    if (actualVolumeObj != null) {
                        double actualVolume = actualVolumeObj instanceof Double ? 
                            (Double) actualVolumeObj : 
                            Double.parseDouble(actualVolumeObj.toString());
                        actualByDate.put(dateStr, actualByDate.getOrDefault(dateStr, 0.0) + actualVolume);
                    }
                    
                    // Get planned volume
                    Object plannedTargetObj = record.get("dailyPlannedTarget");
                    if (plannedTargetObj != null) {
                        double plannedVolume = plannedTargetObj instanceof Double ? 
                            (Double) plannedTargetObj : 
                            Double.parseDouble(plannedTargetObj.toString());
                        plannedByDate.put(dateStr, plannedByDate.getOrDefault(dateStr, 0.0) + plannedVolume);
                    }
                }
            }
            
            // Create trends sorted by date
            actualByDate.keySet().stream()
                .sorted()
                .forEach(date -> {
                    double actual = actualByDate.getOrDefault(date, 0.0);
                    double planned = plannedByDate.getOrDefault(date, 0.0);
                    
                    trends.add(new ProductionTrendDto(
                        date, 
                        Math.round(actual * 10.0) / 10.0,
                        Math.round(planned * 10.0) / 10.0
                    ));
                });
            
            System.out.println("✅ DEBUG: Generated " + trends.size() + " production trends from real data");
        } else {
            // Fallback to mock data if no production records
            trends.add(new ProductionTrendDto("2024-02-10", 850, 1000));
            trends.add(new ProductionTrendDto("2024-02-11", 920, 1000));
            trends.add(new ProductionTrendDto("2024-02-12", 980, 1000));
            System.out.println("⚠️ DEBUG: Using mock production trends (no real data)");
        }
        
        response.setProductionTrends(trends);

        // 5. Predictions - Generate from actual work orders
        List<MaintenancePredictionDto> predictions = new ArrayList<>();
        
        // Debug logging
        System.out.println("🔍 DEBUG: Total work orders fetched: " + workOrders.size());
        
        for (Map<String, Object> workOrder : workOrders) {
            try {
                String status = (String) workOrder.get("status");
                System.out.println("🔍 DEBUG: Work Order - Status: " + status + ", Data: " + workOrder);
                
                // Show all work orders that need attention (not completed or cancelled)
                if (status != null && 
                    (status.equalsIgnoreCase("PENDING") || 
                     status.equalsIgnoreCase("IN_PROGRESS") ||
                     status.equalsIgnoreCase("SCHEDULED") ||
                     status.equalsIgnoreCase("OVERDUE"))) {
                    
                    // Extract workOrderId
                    Object workOrderIdObj = workOrder.get("workOrderId");
                    Long workOrderId = null;
                    if (workOrderIdObj != null) {
                        if (workOrderIdObj instanceof Long) {
                            workOrderId = (Long) workOrderIdObj;
                        } else if (workOrderIdObj instanceof Integer) {
                            workOrderId = ((Integer) workOrderIdObj).longValue();
                        } else {
                            workOrderId = Long.parseLong(workOrderIdObj.toString());
                        }
                    }
                    
                    Object assetIdObj = workOrder.get("assetId");
                    Long assetId = null;
                    if (assetIdObj != null) {
                        if (assetIdObj instanceof Long) {
                            assetId = (Long) assetIdObj;
                        } else if (assetIdObj instanceof Integer) {
                            assetId = ((Integer) assetIdObj).longValue();
                        } else {
                            assetId = Long.parseLong(assetIdObj.toString());
                        }
                    }
                    
                    String assetName = (String) workOrder.get("assetName");
                    // Try both field names - scheduledDate or expectedCompletionDate
                    String dueDate = (String) workOrder.get("expectedCompletionDate");
                    if (dueDate == null || dueDate.isEmpty()) {
                        dueDate = (String) workOrder.get("scheduledDate");
                    }
                    String priority = (String) workOrder.get("priority");
                    
                    System.out.println("🔍 DEBUG: Creating prediction - WorkOrderId: " + workOrderId + ", AssetId: " + assetId + ", AssetName: " + assetName + ", DueDate: " + dueDate + ", Priority: " + priority);
                    
                    if (workOrderId != null && assetId != null && assetName != null && dueDate != null && priority != null) {
                        // Calculate confidence based on priority
                        int confidence = 85;
                        if (priority.equalsIgnoreCase("high")) {
                            confidence = 90;
                        } else if (priority.equalsIgnoreCase("medium")) {
                            confidence = 80;
                        } else if (priority.equalsIgnoreCase("low")) {
                            confidence = 70;
                        }
                        
                        predictions.add(new MaintenancePredictionDto(
                            workOrderId,
                            assetId, 
                            assetName, 
                            dueDate, 
                            priority.toLowerCase(), 
                            confidence
                        ));
                        System.out.println("✅ DEBUG: Prediction added successfully!");
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error processing work order for prediction: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("🔍 DEBUG: Total predictions created: " + predictions.size());
        
        // If no work orders found, add a mock prediction for demonstration
        if (predictions.isEmpty() && !assets.isEmpty()) {
            System.out.println("⚠️ DEBUG: No predictions found, using mock data");
            predictions.add(new MaintenancePredictionDto(
                0L, // mock workOrderId
                assets.get(0).getAssetId(), 
                assets.get(0).getName(), 
                "2024-02-28", 
                "high", 
                85
            ));
        }
        
        response.setMaintenancePredictions(predictions);

        return response;
    }
}