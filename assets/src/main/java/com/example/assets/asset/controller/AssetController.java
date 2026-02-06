package com.example.assets.asset.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.assets.asset.service.AssetService;

import com.example.assets.asset.dto.AssetRequestDTO;
import com.example.assets.asset.dto.AssetResponseDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetController {

    private final AssetService service;

    public AssetController(AssetService service) {
        this.service = service;
    }

    @PostMapping
    public AssetResponseDTO create(@Valid @RequestBody AssetRequestDTO dto) {
        return service.createAsset(dto);
    }

    @GetMapping
    public List<AssetResponseDTO> getAll() {
        return service.getAllAssets();
    }

    @GetMapping("/{id}")
    public AssetResponseDTO get(@PathVariable Long id) {
        return service.getAssetById(id);
    }

    @PutMapping("/{id}")
    public AssetResponseDTO update(@PathVariable Long id,
                                   @Valid @RequestBody AssetRequestDTO dto) {
        return service.updateAsset(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteAsset(id);
    }
    @GetMapping("/exists/{id}")
    public boolean exists(@PathVariable Long id) {
        return service.existsById(id);
    }

}
