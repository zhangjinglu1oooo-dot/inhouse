package com.inhouse.gateway;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关权限校验请求体。
 */
public class PermissionCheckRequest {
    // 应用
    private String app;
    // 功能
    private String feature;
    // 资源标识
    private String resource;
    // 额外属性
    private Map<String, Object> attributes = new HashMap<String, Object>();

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
