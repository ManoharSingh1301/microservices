package com.example.assets.asset.dto;
import com.example.assets.asset.enums.AssetStatus;
import com.example.assets.asset.enums.AssetType;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
 
public class AssetRequestDTO {
 
    @NotBlank
    private String name;
 
    @NotNull
    private AssetType type;
 
    private String location;
 
    private AssetStatus status;
 
    // Explicit getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
 
    public AssetType getType() { return type; }
    public void setType(AssetType type) { this.type = type; }
 
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
 
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
}
 
 