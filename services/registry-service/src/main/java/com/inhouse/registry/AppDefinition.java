package com.inhouse.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 应用注册信息模型。
 */
public class AppDefinition {
    // 应用 ID
    private String id;
    // 应用名称
    private String name;
    // 版本号
    private String version;
    // 发布状态
    private String status = "disabled";
    // 入口地址
    private String entryUrl;
    // 说明
    private String description;
    // 功能列表
    private List<String> features = new ArrayList<String>();
    // 标签
    private List<String> tags = new ArrayList<String>();
    // 创建时间
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntryUrl() {
        return entryUrl;
    }

    public void setEntryUrl(String entryUrl) {
        this.entryUrl = entryUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
