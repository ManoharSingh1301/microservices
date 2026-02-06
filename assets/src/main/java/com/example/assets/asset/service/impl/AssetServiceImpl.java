package com.example.assets.asset.service.impl;

import com.example.assets.asset.dto.AssetRequestDTO;
import com.example.assets.asset.dto.AssetResponseDTO;
import com.example.assets.asset.entity.Asset;
import com.example.assets.asset.enums.AssetStatus;
import com.example.assets.asset.repository.AssetRepository;
import com.example.assets.asset.service.AssetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    private final AssetRepository repo;

    public AssetServiceImpl(AssetRepository repo) {
        this.repo = repo;
    }
    //create asset
    @Override
    public AssetResponseDTO createAsset(AssetRequestDTO dto) {
        Asset asset = new Asset();
        asset.setName(dto.getName());
        asset.setType(dto.getType());
        asset.setLocation(dto.getLocation());
        //default status
        asset.setStatus(AssetStatus.REGISTERED);

        return map(repo.save(asset));
    }
    //get all assets
    @Override
    public List<AssetResponseDTO> getAllAssets() {
        return repo.findAll()
                   .stream()
                   .map(this::map)
                   .collect(Collectors.toList());
    }

    //get asset by id
    @Override
    public AssetResponseDTO getAssetById(Long id) {
        return map(repo.findById(id).orElseThrow());
    }

    //update asset
    @Override
    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO dto) {
        Asset asset = repo.findById(id).orElseThrow();

        asset.setName(dto.getName());
        asset.setType(dto.getType());
        asset.setLocation(dto.getLocation());
        //(lifecycle update)
        if (dto.getStatus() != null) {
            asset.setStatus(dto.getStatus());
        }

        return map(repo.save(asset));
    }

    //delete asset
    @Override
    public void deleteAsset(Long id) {
        repo.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    //mapper
    private AssetResponseDTO map(Asset asset) {
        AssetResponseDTO dto = new AssetResponseDTO();
        dto.setAssetId(asset.getAssetId());
        dto.setName(asset.getName());
        dto.setType(asset.getType());
        dto.setLocation(asset.getLocation());
        dto.setStatus(asset.getStatus());
        return dto;
    }
}
