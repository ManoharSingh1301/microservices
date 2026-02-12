package com.petromanage.dashboard_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetStatusDto {
    private Long id;
    private String name;
    private int utilization;
    private String status;
}