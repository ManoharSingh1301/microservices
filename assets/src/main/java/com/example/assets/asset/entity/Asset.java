package com.example.assets.asset.entity;
 
import java.time.LocalDateTime;
 
import com.example.assets.asset.enums.AssetStatus;
import com.example.assets.asset.enums.AssetType;
 
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
 
@Entity
@Table(name = "assets")
public class Asset {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assetId;
    private String name;
 
    @Enumerated(EnumType.STRING)
    private AssetType type;
 
    private String location;
 
    @Enumerated(EnumType.STRING)
    private AssetStatus status;
 
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
 
    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = AssetStatus.ACTIVE;
    }
 
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
 
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
 
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
 
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
 
 