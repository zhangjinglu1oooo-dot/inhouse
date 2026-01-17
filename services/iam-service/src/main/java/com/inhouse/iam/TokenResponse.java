package com.inhouse.iam;

import java.util.Date;

/**
 * Token 返回对象。
 */
public class TokenResponse {
    // access token
    private String accessToken;
    // token 类型
    private String tokenType = "bearer";
    // 过期时间
    private Date expiresAt;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
