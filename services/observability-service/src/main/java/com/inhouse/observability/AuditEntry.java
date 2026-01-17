package com.inhouse.observability;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志条目。
 */
public class AuditEntry {
    // 审计 ID
    private String id;
    // 操作名称
    private String action;
    // 操作人
    private String actor;
    // 目标对象
    private String target;
    // 详情信息
    private Map<String, Object> detail = new HashMap<String, Object>();
    // 创建时间
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
