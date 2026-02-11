package com.example.assets.asset.dto;
import com.example.assets.asset.enums.AssetType;
import com.example.assets.asset.enums.AssetStatus;
 
public class AssetResponseDTO {
 
    private Long assetId;
    private String name;
    private AssetType type;
    private String location;
    private AssetStatus status;
 
    // Explicit getters and setters
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
 
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
 
    public AssetType getType() { return type; }
    public void setType(AssetType type) { this.type = type; }
 
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
 
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
}
 
 