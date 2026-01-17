package com.inhouse.iam;

/**
 * Token 校验请求体。
 */
public class TokenRequest {
    // access token
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
