package com.inhouse.gateway;

/**
 * 网关权限校验响应体。
 */
public class PermissionCheckResponse {
    // 是否允许
    private boolean allowed;
    // 决策原因
    private String decisionReason;

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
}
