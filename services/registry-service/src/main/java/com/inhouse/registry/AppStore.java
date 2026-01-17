package com.inhouse.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用注册内存存储。
 */
public class AppStore {
    // 并发安全的应用集合
    private final Map<String, AppDefinition> apps = new ConcurrentHashMap<String, AppDefinition>();

    public Map<String, AppDefinition> getApps() {
        return apps;
    }
}
