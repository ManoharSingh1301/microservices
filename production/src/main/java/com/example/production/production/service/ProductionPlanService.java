package com.example.production.production.service;

import com.example.production.production.dto.*;
import com.example.production.production.entity.ProductionPlan;
import com.example.production.production.feign.AssetClient;
import com.example.production.production.repository.ProductionPlanRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductionPlanService {

    private final ProductionPlanRepository repository;
    private final AssetClient assetClient;

    public ProductionPlanService(ProductionPlanRepository repository, AssetClient assetClient) {
        this.repository = repository;
        this.assetClient = assetClient;
    }

    // ✅ CREATE
    public ProductionPlanResponseDTO savePlan(ProductionPlanRequestDTO dto) {
        assetClient.getAssetById(dto.getAssetId());

        ProductionPlan plan = new ProductionPlan();
        plan.setAssetId(dto.getAssetId());
        plan.setPlannedVolume(dto.getPlannedVolume());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());

        return map(repository.save(plan));
    }

    // ✅ READ ALL
    public List<ProductionPlanResponseDTO> getAllPlans() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    // ✅ READ BY ID
    public ProductionPlanResponseDTO getPlanById(Long id) {
        ProductionPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id " + id));
        return map(plan);
    }

    // ✅ UPDATE
    public ProductionPlanResponseDTO updatePlan(Long id, ProductionPlanRequestDTO dto) {
        ProductionPlan plan = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id " + id));

        plan.setAssetId(dto.getAssetId());
        plan.setPlannedVolume(dto.getPlannedVolume());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());

        return map(repository.save(plan));
    }

    // ✅ DEV / FEIGN TEST
    public void processProduction() {
        assetClient.getAllAssets();
    }

    // 🔁 MAPPER
    private ProductionPlanResponseDTO map(ProductionPlan plan) {
        AssetDTO asset = assetClient.getAssetById(plan.getAssetId());

        ProductionPlanResponseDTO dto = new ProductionPlanResponseDTO();
        dto.setPlanId(plan.getPlanId());
        dto.setAssetId(plan.getAssetId());
        dto.setPlannedVolume(plan.getPlannedVolume());
        dto.setStartDate(plan.getStartDate());
        dto.setEndDate(plan.getEndDate());
        dto.setAssetDetails(asset);

        return dto;
    }
}
