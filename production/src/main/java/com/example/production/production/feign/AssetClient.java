package com.example.production.production.feign;

import com.example.production.production.dto.AssetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ASSETS-SERVICE")
public interface AssetClient {

    @GetMapping("/api/assets/{id}")
    AssetDTO getAssetById(@PathVariable Long id);

    @GetMapping("/api/assets")
    List<AssetDTO> getAllAssets();
}
