package com.inhouse.permission;

public class PermissionCheckResponse {
    private boolean allowed;
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
