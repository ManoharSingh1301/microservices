package com.example.maintenance.maintenance.repository;

import com.example.maintenance.maintenance.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
}
