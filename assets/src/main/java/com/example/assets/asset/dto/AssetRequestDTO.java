package com.example.assets.asset.dto;
import com.example.assets.asset.enums.AssetStatus;
import com.example.assets.asset.enums.AssetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private AssetType type;

    private String location;

    private AssetStatus status;
}
