package com.example.production.production.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
    
    @Column(length = 20)
    private String status; 

    
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<ProductionRecord> records = new ArrayList<>();

    
    public Double getDailyPlannedVolume() {
        if (plannedVolume == null || startDate == null || endDate == null) {
            return 0.0;
        }
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        
        if (daysBetween <= 0) {
            return 0.0;
        }
        
        return plannedVolume / daysBetween;
    }
}