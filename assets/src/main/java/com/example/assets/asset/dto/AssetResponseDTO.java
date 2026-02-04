package com.example.assets.asset.dto;
import com.example.assets.asset.enums.AssetType;
import com.example.assets.asset.enums.AssetStatus;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetResponseDTO {

    private Long assetId;
    private String name;
    private AssetType type;
    private String location;
    private AssetStatus status;
}
