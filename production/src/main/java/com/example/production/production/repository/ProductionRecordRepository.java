package com.example.production.production.repository;

import com.example.production.production.entity.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductionRecordRepository
        extends JpaRepository<ProductionRecord, Long> {

    List<ProductionRecord> findByPlan_PlanId(Long planId);
}
