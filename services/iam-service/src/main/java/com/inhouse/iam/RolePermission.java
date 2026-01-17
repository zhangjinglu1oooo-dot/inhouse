package com.inhouse.iam;

/**
 * 角色权限定义。
 */
public class RolePermission {
    // 应用名称
    private String app;
    // 功能点
    private String feature;
    // 资源标识
    private String resource;
    // 允许或拒绝
    private String effect = "allow";

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
}
