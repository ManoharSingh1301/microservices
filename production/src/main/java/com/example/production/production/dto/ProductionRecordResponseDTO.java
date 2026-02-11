package com.example.production.production.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionRecordResponseDTO {
    private Long recordId;
    private Long planId;
    private Long assetId;
    private Double actualVolume;
    private LocalDate date;
    private AssetDTO assetDetails;
    private Double dailyPlannedTarget;
}
