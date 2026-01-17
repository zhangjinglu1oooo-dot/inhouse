package com.inhouse.permission;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PolicyStore {
    private final Map<String, Policy> policies = new ConcurrentHashMap<String, Policy>();

    public Map<String, Policy> getPolicies() {
        return policies;
    }
}
