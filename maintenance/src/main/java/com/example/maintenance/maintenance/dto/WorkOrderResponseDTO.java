package com.example.maintenance.maintenance.dto;

import lombok.Data;

@Data
public class WorkOrderResponseDTO {

    private Long workOrderId;

    private Long assetId;
    private String assetName;

    private String description;
    private String maintenanceType;
    private String scheduledDate;

    private String expectedCompletionDate;
    private String actualCompletionDate;

    private String priority;
    private String status;
    private String technicianName;
}
