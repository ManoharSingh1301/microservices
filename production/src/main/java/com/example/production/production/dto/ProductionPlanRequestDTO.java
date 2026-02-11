package com.example.production.production.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionPlanRequestDTO {
    private Long assetId;
    private Double plannedVolume;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; 
}
