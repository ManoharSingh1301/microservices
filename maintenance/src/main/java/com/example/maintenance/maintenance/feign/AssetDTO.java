package com.example.maintenance.maintenance.feign;

import lombok.Data;

@Data
public class AssetDTO {
    private Long assetId;
    private String name;
    private String type;
    private String status;
}
