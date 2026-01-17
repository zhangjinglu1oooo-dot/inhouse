package com.inhouse.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppStore {
    private final Map<String, AppDefinition> apps = new ConcurrentHashMap<String, AppDefinition>();

    public Map<String, AppDefinition> getApps() {
        return apps;
    }
}
