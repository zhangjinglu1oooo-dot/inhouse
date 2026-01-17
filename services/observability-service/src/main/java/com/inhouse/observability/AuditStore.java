package com.inhouse.observability;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 审计日志内存存储。
 */
public class AuditStore {
    // 线程安全审计集合
    private final List<AuditEntry> audits = new CopyOnWriteArrayList<AuditEntry>();

    public List<AuditEntry> getAudits() {
        return audits;
    }
}
