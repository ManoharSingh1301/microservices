package com.example.compliance.compliance.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.compliance.compliance.entity.Audit;
import com.example.compliance.compliance.repository.AuditRepository;

@Service
public class AuditService {

    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    public Audit createAudit(Audit audit) {
        return auditRepository.save(audit);
    }

    public void deleteAllAudits() {
        auditRepository.deleteAll();
    }

    public int getCount() {
        return (int) auditRepository.count();
    }
}
