package com.example.compliance.compliance.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.compliance.compliance.service.AuditService;
import com.example.compliance.compliance.entity.Audit;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/compliance/audit-log")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<Audit> getAllAudits() {
        return auditService.getAllAudits();
    }

    @DeleteMapping
    public void deleteAllAudits() {
        auditService.deleteAllAudits();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getAuditCount() {
        return ResponseEntity.ok(auditService.getCount());
    }
}
