package com.example.compliance.compliance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.compliance.compliance.entity.ComplianceReport;
import com.example.compliance.compliance.service.ComplianceReportService;

@RestController
@RequestMapping("/api/compliance/reports")
public class ComplianceReportController {

    private final ComplianceReportService service;

    public ComplianceReportController(ComplianceReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ComplianceReport> submitReport(
            @RequestBody ComplianceReport report) {
        return ResponseEntity.ok(service.saveReport(report));
    }

    @GetMapping
    public List<ComplianceReport> getAllReports() {
        return service.getAllReports();
    }

    @GetMapping("/{id}")
    public ComplianceReport getReportById(@PathVariable Long id) {
        return service.getReportById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplianceReport> updateReport(
            @PathVariable Long id,
            @RequestBody ComplianceReport reportDetails) {
        return ResponseEntity.ok(service.updateReport(id, reportDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        service.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getReportCount() {
        return ResponseEntity.ok(service.getCount());
    }
}
