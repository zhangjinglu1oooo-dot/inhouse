package com.inhouse.permission;

/**
 * 权限校验响应体。
 */
public class PermissionCheckResponse {
    // 是否允许
    private boolean allowed;
    // 决策原因
    private String decisionReason;

    public PermissionCheckResponse() {
    }

    public PermissionCheckResponse(boolean allowed, String decisionReason) {
        this.allowed = allowed;
        this.decisionReason = decisionReason;
    }

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
