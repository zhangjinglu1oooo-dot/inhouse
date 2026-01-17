package com.inhouse.observability;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuditStore {
    private final List<AuditEntry> audits = new CopyOnWriteArrayList<AuditEntry>();

    public List<AuditEntry> getAudits() {
        return audits;
    }
}
