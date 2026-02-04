package com.example.compliance.compliance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.compliance.compliance.enums.ComplianceStatus;
import com.example.compliance.compliance.enums.ReportType;

import jakarta.persistence.*;

@Entity
@Table(name = "compliance_reports")
public class ComplianceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    // 🔑 Microservice-safe reference
    @Column(nullable = false)
    private Long assetId;

    // Cached for display (fetched via Feign)
    @Column(nullable = false)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    private Integer safetyScore;

    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    private String inspector;

    private LocalDate nextAuditDate;
    private LocalDate generatedDate;
    private LocalDateTime lastUpdatedDate;

    @PrePersist
    public void onCreate() {
        this.lastUpdatedDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdatedDate = LocalDateTime.now();
    }

    // ===== Getters & Setters =====

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Integer getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(Integer safetyScore) {
        this.safetyScore = safetyScore;
    }

    public ComplianceStatus getComplianceStatus() {
        return complianceStatus;
    }

    public void setComplianceStatus(ComplianceStatus complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public LocalDate getNextAuditDate() {
        return nextAuditDate;
    }

    public void setNextAuditDate(LocalDate nextAuditDate) {
        this.nextAuditDate = nextAuditDate;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
