package com.example.assets.asset.service;
 
import java.util.List;
 
import com.example.assets.asset.dto.AssetRequestDTO;
import com.example.assets.asset.dto.AssetResponseDTO;
import com.example.assets.asset.enums.AssetStatus;
 
public interface AssetService {
 
    AssetResponseDTO createAsset(AssetRequestDTO dto);
    List<AssetResponseDTO> getAllAssets();
    AssetResponseDTO getAssetById(Long id);
    AssetResponseDTO updateAsset(Long id, AssetRequestDTO dto);
    void deleteAsset(Long id);
    boolean existsById(Long id);
    List<AssetResponseDTO> getAssetsByStatus(AssetStatus status);
 
}