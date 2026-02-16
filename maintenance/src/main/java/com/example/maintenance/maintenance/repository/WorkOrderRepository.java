package com.example.maintenance.maintenance.repository;

import com.example.maintenance.maintenance.entity.WorkOrder;
import com.example.maintenance.maintenance.enums.WorkOrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    long countByStatusNot(WorkOrderStatus status);
}
