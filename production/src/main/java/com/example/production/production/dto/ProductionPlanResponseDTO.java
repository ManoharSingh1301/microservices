package com.example.production.production.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionPlanResponseDTO {
    private Long planId;
    private Long assetId;
    private Double plannedVolume;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; 
    private AssetDTO assetDetails;
}
