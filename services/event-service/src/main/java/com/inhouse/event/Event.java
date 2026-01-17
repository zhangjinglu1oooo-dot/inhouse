package com.inhouse.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件模型。
 */
public class Event {
    // 事件 ID
    private String id;
    // 事件主题
    private String topic;
    // 事件负载
    private Map<String, Object> payload = new HashMap<String, Object>();
    // 创建时间
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
