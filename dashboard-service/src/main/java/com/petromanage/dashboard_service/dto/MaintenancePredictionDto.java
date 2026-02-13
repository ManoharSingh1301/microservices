package com.petromanage.dashboard_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaintenancePredictionDto {
    private Long workOrderId;
    private Long assetId;
    private String assetName;
    private String predictedDate;
    private String priority;
    private int confidence;
}