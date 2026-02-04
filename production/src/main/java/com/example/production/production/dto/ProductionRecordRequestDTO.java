package com.example.production.production.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProductionRecordRequestDTO {
    private Long planId;
    private Double actualVolume;
    private LocalDate date;
}
