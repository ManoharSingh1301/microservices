package com.example.compliance.compliance.feign;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.compliance.compliance.dto.AssetDTO;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "ASSETS-SERVICE")
public interface AssetFeignClient {

    @GetMapping("/api/assets/exists/{id}")
    boolean assetExists(@PathVariable("id") Long id);

    @GetMapping("/api/assets/{id}")
    AssetDTO getAsset(@PathVariable("id") Long id);
}


