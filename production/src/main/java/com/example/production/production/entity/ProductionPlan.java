package com.example.production.production.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "production_plans")
public class ProductionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    private Long assetId;
    private Double plannedVolume;
    private LocalDate startDate;
    private LocalDate endDate;
}
