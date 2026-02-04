package com.example.maintenance.maintenance.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ASSETS-SERVICE")
public interface AssetFeignClient {

    @GetMapping("/api/assets/exists/{id}")
    boolean assetExists(@PathVariable("id") Long id);

    @GetMapping("/api/assets/{id}")
    AssetDTO getAsset(@PathVariable("id") Long id);
}
