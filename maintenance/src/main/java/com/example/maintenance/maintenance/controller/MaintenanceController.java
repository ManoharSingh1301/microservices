package com.example.maintenance.maintenance.controller;
import com.example.maintenance.maintenance.dto.WorkOrderRequestDTO;
import com.example.maintenance.maintenance.dto.WorkOrderResponseDTO;
import com.example.maintenance.maintenance.service.MaintenanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/work-orders")
    public ResponseEntity<WorkOrderResponseDTO> create(
            @RequestBody WorkOrderRequestDTO dto) {
        return ResponseEntity.ok(maintenanceService.createWorkOrder(dto));
    }

    @PatchMapping("/work-orders/{id}/update-progress")
    public ResponseEntity<WorkOrderResponseDTO> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(
                maintenanceService.updateWorkOrderProgress(id, updates));
    }

    @GetMapping("/work-orders")
    public ResponseEntity<List<WorkOrderResponseDTO>> getAll() {
        return ResponseEntity.ok(maintenanceService.getAllWorkOrders());
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getWorkOrderCountNotCompleted() {
        return ResponseEntity.ok(maintenanceService.getIncompleteCount());
    }
}
