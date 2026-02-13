package com.petromanage.dashboard_service.dto;
import lombok.Data;

@Data
public class CurrentMetricsDto {
    private double productionEfficiency;
    private double efficiencyChange;
    private double assetUtilization;
    private double utilizationChange;
    private int maintenanceDue;
}