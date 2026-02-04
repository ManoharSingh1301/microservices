package com.example.maintenance.maintenance.entity;

import com.example.maintenance.maintenance.enums.MaintenanceType;
import com.example.maintenance.maintenance.enums.WorkOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workOrderId;

    // 🔥 ONLY REFERENCE (NO Asset entity)
    private Long assetId;

    private String description;
    private LocalDate scheduledDate;

    private LocalDate expectedCompletionDate;
    private LocalDate actualCompletionDate;

    private String assignedTechnician;
    private String priority;

    @Enumerated(EnumType.STRING)
    private MaintenanceType maintenanceType;

    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;
}
