package com.example.production.production.controller;

import com.example.production.production.dto.ProductionRecordRequestDTO;
import com.example.production.production.dto.ProductionRecordResponseDTO;
import com.example.production.production.service.ProductionRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/production/records")
public class ProductionRecordController {

    private final ProductionRecordService service;

    public ProductionRecordController(ProductionRecordService service) {
        this.service = service;
    }

    // ✅ Create production record
    @PostMapping
    public ResponseEntity<ProductionRecordResponseDTO> createRecord(
            @RequestBody ProductionRecordRequestDTO dto) {
        return new ResponseEntity<>(service.saveRecord(dto), HttpStatus.CREATED);
    }

    // ✅ Get all records (Dashboard aggregation)
    @GetMapping
    public ResponseEntity<List<ProductionRecordResponseDTO>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }

    // ✅ Get record by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductionRecordResponseDTO> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRecordById(id));
    }

    // ✅ Update record
    @PutMapping("/{id}")
    public ResponseEntity<ProductionRecordResponseDTO> updateRecord(
            @PathVariable Long id,
            @RequestBody ProductionRecordRequestDTO dto) {
        return ResponseEntity.ok(service.updateRecord(id, dto));
    }

    // ✅ Records by Plan (very important for dashboard)
    @GetMapping("/plan/{planId}")
    public ResponseEntity<List<ProductionRecordResponseDTO>> getByPlan(
            @PathVariable Long planId) {
        return ResponseEntity.ok(service.getRecordsByPlan(planId));
    }
}
