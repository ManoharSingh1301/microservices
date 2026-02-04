package com.example.maintenance.maintenance.dto;

import lombok.Data;

@Data
public class WorkOrderRequestDTO {

    private Long assetId;
    private String description;
    private String scheduledDate;        // yyyy-MM-dd
    private String maintenanceType;      // PREVENTIVE / CORRECTIVE
    private String priority;             // HIGH / MEDIUM / LOW
    private String technicianName;
}

