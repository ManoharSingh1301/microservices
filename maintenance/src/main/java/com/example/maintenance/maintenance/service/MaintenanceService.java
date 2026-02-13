package com.example.maintenance.maintenance.service;

import com.example.maintenance.maintenance.dto.WorkOrderRequestDTO;
import com.example.maintenance.maintenance.dto.WorkOrderResponseDTO;
import com.example.maintenance.maintenance.entity.WorkOrder;
import com.example.maintenance.maintenance.enums.MaintenanceType;
import com.example.maintenance.maintenance.enums.WorkOrderStatus;
import com.example.maintenance.maintenance.feign.AssetDTO;
import com.example.maintenance.maintenance.feign.AssetFeignClient;
import com.example.maintenance.maintenance.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class MaintenanceService {

    private final WorkOrderRepository workOrderRepository;
    private final AssetFeignClient assetFeignClient;

    public MaintenanceService(
            WorkOrderRepository workOrderRepository,
            AssetFeignClient assetFeignClient) {
        this.workOrderRepository = workOrderRepository;
        this.assetFeignClient = assetFeignClient;
    }

    /* ================= CREATE ================= */
    @Transactional
    public WorkOrderResponseDTO createWorkOrder(WorkOrderRequestDTO dto) {

        if (!assetFeignClient.assetExists(dto.getAssetId())) {
            throw new RuntimeException("Invalid Asset ID");
        }

        AssetDTO asset = assetFeignClient.getAsset(dto.getAssetId());

        

        WorkOrder order = new WorkOrder();
        order.setAssetId(dto.getAssetId());
        order.setDescription(dto.getDescription());
        order.setScheduledDate(LocalDate.parse(dto.getScheduledDate()));
        order.setMaintenanceType(
                MaintenanceType.valueOf(dto.getMaintenanceType().toUpperCase()));
        order.setPriority(dto.getPriority().toUpperCase());
        order.setAssignedTechnician(dto.getTechnicianName());
        order.setStatus(WorkOrderStatus.SCHEDULED);

        return map(order, asset, workOrderRepository.save(order));
    }

    /* ================= UPDATE ================= */
    @Transactional
    public WorkOrderResponseDTO updateWorkOrderProgress(
            Long id, Map<String, Object> updates) {

        WorkOrder order = workOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Work Order not found"));

        if (updates.containsKey("expectedCompletionDate")) {
            order.setExpectedCompletionDate(
                    LocalDate.parse((String) updates.get("expectedCompletionDate")));
        }

        if (updates.containsKey("actualCompletionDate")) {
            LocalDate actual = LocalDate.parse(
                    (String) updates.get("actualCompletionDate"));
            order.setActualCompletionDate(actual);

            if (order.getExpectedCompletionDate() != null &&
                actual.isAfter(order.getExpectedCompletionDate())) {
                order.setStatus(WorkOrderStatus.OVERDUE);
            } else {
                order.setStatus(WorkOrderStatus.COMPLETED);
            }
        } else if (updates.containsKey("status")) {
            order.setStatus(
                    WorkOrderStatus.valueOf(
                            updates.get("status").toString()
                                    .toUpperCase()
                                    .replace(" ", "_")));
        }

        AssetDTO asset = assetFeignClient.getAsset(order.getAssetId());
        return map(order, asset, workOrderRepository.save(order));
    }

    /* ================= GET ALL ================= */
    public List<WorkOrderResponseDTO> getAllWorkOrders() {
        return workOrderRepository.findAll()
                .stream()
                .map(order -> {
                    AssetDTO asset = assetFeignClient.getAsset(order.getAssetId());
                    return map(order, asset, order);
                })
                .collect(Collectors.toList());
    }

    /* ================= MAPPER ================= */
    private WorkOrderResponseDTO map(
            WorkOrder order, AssetDTO asset, WorkOrder saved) {

        WorkOrderResponseDTO dto = new WorkOrderResponseDTO();
        dto.setWorkOrderId(saved.getWorkOrderId());
        dto.setAssetId(saved.getAssetId());
        dto.setAssetName(asset.getName());
        dto.setDescription(saved.getDescription());
        dto.setMaintenanceType(saved.getMaintenanceType().name());
        dto.setScheduledDate(saved.getScheduledDate().toString());
        dto.setPriority(saved.getPriority());
        dto.setStatus(saved.getStatus().name());
        dto.setTechnicianName(saved.getAssignedTechnician());

        if (saved.getExpectedCompletionDate() != null)
            dto.setExpectedCompletionDate(
                    saved.getExpectedCompletionDate().toString());

        if (saved.getActualCompletionDate() != null)
            dto.setActualCompletionDate(
                    saved.getActualCompletionDate().toString());

        return dto;
    }
}
