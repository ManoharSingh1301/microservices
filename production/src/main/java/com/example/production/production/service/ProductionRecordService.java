package com.example.production.production.service;

import com.example.production.production.dto.*;
import com.example.production.production.entity.ProductionPlan;
import com.example.production.production.entity.ProductionRecord;
import com.example.production.production.feign.AssetClient;
import com.example.production.production.repository.ProductionPlanRepository;
import com.example.production.production.repository.ProductionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionRecordService {

    private final ProductionRecordRepository recordRepository;
    private final ProductionPlanRepository planRepository;
    private final AssetClient assetClient;

    public ProductionRecordService(
            ProductionRecordRepository recordRepository,
            ProductionPlanRepository planRepository,
            AssetClient assetClient) {
        this.recordRepository = recordRepository;
        this.planRepository = planRepository;
        this.assetClient = assetClient;
    }

    
    public ProductionRecordResponseDTO saveRecord(ProductionRecordRequestDTO dto) {

        ProductionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Production Plan not found with id " + dto.getPlanId()));

        ProductionRecord record = new ProductionRecord();
        record.setPlan(plan);
        record.setAssetId(plan.getAssetId());
        record.setActualVolume(dto.getActualVolume());
        record.setDate(dto.getDate());

        ProductionRecord saved = recordRepository.save(record);
        return map(saved);
    }

    
    public List<ProductionRecordResponseDTO> getAllRecords() {
        return recordRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    
    public ProductionRecordResponseDTO getRecordById(Long id) {
        ProductionRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Record not found with id " + id));
        return map(record);
    }

    
    public ProductionRecordResponseDTO updateRecord(Long id, ProductionRecordRequestDTO dto) {

        ProductionRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Record not found with id " + id));

        ProductionPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Plan not found with id " + dto.getPlanId()));

        record.setPlan(plan);
        record.setAssetId(plan.getAssetId());
        record.setActualVolume(dto.getActualVolume());
        record.setDate(dto.getDate());

        return map(recordRepository.save(record));
    }

    
    public List<ProductionRecordResponseDTO> getRecordsByPlan(Long planId) {
        return recordRepository.findByPlan_PlanId(planId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    
    private ProductionRecordResponseDTO map(ProductionRecord record) {

        AssetDTO asset = assetClient.getAssetById(record.getAssetId());

        ProductionRecordResponseDTO dto = new ProductionRecordResponseDTO();
        dto.setRecordId(record.getRecordId());
        dto.setPlanId(record.getPlan().getPlanId());
        dto.setAssetId(record.getAssetId());
        dto.setActualVolume(record.getActualVolume());
        dto.setDate(record.getDate());
        dto.setAssetDetails(asset);
        dto.setDailyPlannedTarget(record.getPlan().getDailyPlannedVolume());

        return dto;
    }
}
