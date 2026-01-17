package com.inhouse.permission;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限策略内存存储。
 */
public class PolicyStore {
    // 并发安全的策略集合
    private final Map<String, Policy> policies = new ConcurrentHashMap<String, Policy>();

    public Map<String, Policy> getPolicies() {
        return policies;
    }
}
