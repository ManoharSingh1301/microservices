package com.petromanage.dashboard_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductionTrendDto {
    private String date;
    private double efficiency;
    private double downtime;
}