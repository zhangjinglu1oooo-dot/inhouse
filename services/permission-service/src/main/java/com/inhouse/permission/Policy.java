package com.inhouse.permission;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限策略模型。
 */
public class Policy {
    // 策略 ID
    private String id;
    // 目标应用
    private String app;
    // 功能点
    private String feature;
    // 资源标识
    private String resource;
    // 允许或拒绝
    private String effect = "allow";
    // 附加条件
    private Map<String, Object> conditions = new HashMap<String, Object>();
    // 创建时间
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
