package com.example.compliance.compliance.entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "audit_log")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliance_report_id", nullable = true)
    @JsonIncludeProperties({"reportId"})
    private ComplianceReport complianceReport;

    @Column(name = "persisted_report_id")
    private Long reportIdDisplay;

    private String action;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "performed_by") 
    private String user;

    @Column(name = "audit_timestamp") 
    private String timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss"));
    }

    public Audit() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ComplianceReport getComplianceReport() { return complianceReport; }
    public void setComplianceReport(ComplianceReport complianceReport) { this.complianceReport = complianceReport; }
    public Long getReportIdDisplay() { return reportIdDisplay; }
    public void setReportIdDisplay(Long reportIdDisplay) { this.reportIdDisplay = reportIdDisplay; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}