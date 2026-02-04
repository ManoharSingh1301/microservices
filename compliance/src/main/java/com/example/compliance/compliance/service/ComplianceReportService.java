package com.example.compliance.compliance.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.compliance.compliance.entity.Audit;
import com.example.compliance.compliance.entity.ComplianceReport;
import com.example.compliance.compliance.repository.AuditRepository;
import com.example.compliance.compliance.repository.ComplianceReportRepository;

@Service
public class ComplianceReportService {

    private final ComplianceReportRepository reportRepository;
    private final AuditRepository auditRepository;
    private final AuditService auditService;

    public ComplianceReportService(
            ComplianceReportRepository reportRepository,
            AuditRepository auditRepository,
            AuditService auditService) {
        this.reportRepository = reportRepository;
        this.auditRepository = auditRepository;
        this.auditService = auditService;
    }

    /* ================= CREATE ================= */
    @Transactional
    public ComplianceReport saveReport(ComplianceReport report) {

        ComplianceReport savedReport = reportRepository.save(report);

        String newVal = "Status: " + savedReport.getComplianceStatus()
                + ", Score: " + savedReport.getSafetyScore();

        logAudit(savedReport, "CREATE", "NONE", newVal, savedReport.getInspector());

        return savedReport;
    }

    /* ================= READ ================= */
    public List<ComplianceReport> getAllReports() {
        return reportRepository.findAll();
    }

    public ComplianceReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Compliance report not found with id: " + id));
    }

    /* ================= UPDATE ================= */
    @Transactional
    public ComplianceReport updateReport(Long id, ComplianceReport details) {

        return reportRepository.findById(id).map(report -> {

            String oldStatus = String.valueOf(report.getComplianceStatus());
            Integer oldScore = report.getSafetyScore();
            boolean changed = false;

            if (details.getComplianceStatus() != null &&
                    !details.getComplianceStatus().equals(report.getComplianceStatus())) {
                report.setComplianceStatus(details.getComplianceStatus());
                changed = true;
            }

            if (details.getSafetyScore() != null &&
                    !details.getSafetyScore().equals(report.getSafetyScore())) {
                report.setSafetyScore(details.getSafetyScore());
                changed = true;
            }

            if (details.getInspector() != null)
                report.setInspector(details.getInspector());

            if (details.getNextAuditDate() != null)
                report.setNextAuditDate(details.getNextAuditDate());

            ComplianceReport updated = reportRepository.save(report);

            if (changed) {
                String oldVal = "Status: " + oldStatus + ", Score: " + oldScore;
                String newVal = "Status: " + updated.getComplianceStatus()
                        + ", Score: " + updated.getSafetyScore();

                logAudit(updated, "UPDATE", oldVal, newVal, updated.getInspector());
            }

            return updated;

        }).orElseThrow(() ->
                new RuntimeException("Compliance report not found with id: " + id));
    }

    /* ================= DELETE ================= */
    @Transactional
    public void deleteReport(Long id) {

        ComplianceReport report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Report not found with id " + id));

        // STEP 1: Unlink old audits
        List<Audit> audits = auditRepository.findByComplianceReport(report);
        for (Audit a : audits) {
            a.setComplianceReport(null);
        }
        auditRepository.saveAll(audits);

        // STEP 2: Log delete event
        Audit deleteAudit = new Audit();
        deleteAudit.setAction("DELETE");
        deleteAudit.setOldValue(
                "Status: " + report.getComplianceStatus()
                        + ", Score: " + report.getSafetyScore());
        deleteAudit.setNewValue("NONE");
        deleteAudit.setUser(report.getInspector());
        deleteAudit.setReportIdDisplay(id);

        auditService.createAudit(deleteAudit);

        // STEP 3: Delete report
        reportRepository.delete(report);
    }

    /* ================= AUDIT HELPER ================= */
    private void logAudit(
            ComplianceReport report,
            String action,
            String oldVal,
            String newVal,
            String user) {

        Audit audit = new Audit();
        audit.setComplianceReport(report);
        audit.setReportIdDisplay(report.getReportId());
        audit.setAction(action);
        audit.setOldValue(oldVal);
        audit.setNewValue(newVal);
        audit.setUser(user);

        auditService.createAudit(audit);
    }
}
