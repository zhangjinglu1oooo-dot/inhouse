package com.inhouse.ai;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 调用响应体。
 */
public class AiInvokeResponse {
    // 服务提供商名称
    private String provider;
    // 模型名称
    private String model;
    // 返回内容
    private String output;
    // 使用量统计
    private Map<String, Object> usage = new HashMap<String, Object>();
    // 创建时间
    private Date createdAt;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Map<String, Object> getUsage() {
        return usage;
    }

    public void setUsage(Map<String, Object> usage) {
        this.usage = usage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
