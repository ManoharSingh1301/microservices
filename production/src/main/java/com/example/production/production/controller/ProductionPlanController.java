package com.example.production.production.controller;

import com.example.production.production.dto.ProductionPlanRequestDTO;
import com.example.production.production.dto.ProductionPlanResponseDTO;
import com.example.production.production.service.ProductionPlanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production/plans")
public class ProductionPlanController {

    private final ProductionPlanService service;

    public ProductionPlanController(ProductionPlanService service) {
        this.service = service;
    }

    // ✅ Create Production Plan
    @PostMapping
    public ResponseEntity<ProductionPlanResponseDTO> createPlan(
            @RequestBody ProductionPlanRequestDTO requestDto) {
        return new ResponseEntity<>(service.savePlan(requestDto), HttpStatus.CREATED);
    }

    // ✅ Get all plans (Dashboard-friendly)
    @GetMapping
    public ResponseEntity<List<ProductionPlanResponseDTO>> getAllPlans() {
        return ResponseEntity.ok(service.getAllPlans());
    }

    // ✅ Get plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductionPlanResponseDTO> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPlanById(id));
    }

    // ✅ Update plan
    @PutMapping("/{id}")
    public ResponseEntity<ProductionPlanResponseDTO> updatePlan(
            @PathVariable Long id,
            @RequestBody ProductionPlanRequestDTO requestDto) {
        return ResponseEntity.ok(service.updatePlan(id, requestDto));
    }

    // ✅ Feign sanity check (dev only)
    @GetMapping("/test-assets")
    public ResponseEntity<String> testAssetIntegration() {
        service.processProduction();
        return ResponseEntity.ok("Asset Feign Client working");
    }
}
