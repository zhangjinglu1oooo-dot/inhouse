package com.inhouse.observability;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志控制器。
 */
@RestController
@RequestMapping("/observability/audits")
@CrossOrigin(origins = "*")
public class AuditController {
    // 审计仓库
    private final AuditRepository repository;

    public AuditController(AuditRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditEntry create(@RequestBody AuditEntry entry) {
        // 写入审计日志
        entry.setId(UUID.randomUUID().toString());
        entry.setCreatedAt(new Date());
        repository.saveAudit(entry);
        return entry;
    }

    @GetMapping
    public List<AuditEntry> list() {
        return new ArrayList<AuditEntry>(repository.listAudits());
    }
}
