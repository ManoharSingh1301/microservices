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

    
    @PostMapping
    public ResponseEntity<ProductionPlanResponseDTO> createPlan(
            @RequestBody ProductionPlanRequestDTO requestDto) {
        return new ResponseEntity<>(service.savePlan(requestDto), HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<ProductionPlanResponseDTO>> getAllPlans() {
        return ResponseEntity.ok(service.getAllPlans());
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ProductionPlanResponseDTO> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPlanById(id));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ProductionPlanResponseDTO> updatePlan(
            @PathVariable Long id,
            @RequestBody ProductionPlanRequestDTO requestDto) {
        return ResponseEntity.ok(service.updatePlan(id, requestDto));
    }

    

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductionPlanResponseDTO> deletePlan(@PathVariable Long id) {
        ProductionPlanResponseDTO deletedPlan = service.deletePlan(id);
        return ResponseEntity.ok(deletedPlan);
    }

    
    @GetMapping("/test-assets")
    public ResponseEntity<String> testAssetIntegration() {
        service.processProduction();
        return ResponseEntity.ok("Asset Feign Client working");
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getPlanCount() {
        return ResponseEntity.ok(service.getCount());
    }
}
