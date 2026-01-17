package com.inhouse.iam;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenService {
    private static final byte[] SECRET = "inhouse-secret".getBytes(StandardCharsets.UTF_8);
    private static final long TOKEN_TTL_MILLIS = TimeUnit.HOURS.toMillis(1);

    public TokenResponse issueToken(String userId) {
        long expiresAt = System.currentTimeMillis() + TOKEN_TTL_MILLIS;
        String payload = userId + ":" + expiresAt;
        String signature = sign(payload);
        String token = Base64.getUrlEncoder().encodeToString((payload + "." + signature).getBytes(StandardCharsets.UTF_8));
        TokenResponse response = new TokenResponse();
        response.setAccessToken(token);
        response.setExpiresAt(new Date(expiresAt));
        return response;
    }

    public String validate(String token) {
        byte[] decoded = Base64.getUrlDecoder().decode(token);
        String tokenBody = new String(decoded, StandardCharsets.UTF_8);
        String[] parts = tokenBody.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid token");
        }
        String payload = parts[0];
        String signature = parts[1];
        if (!sign(payload).equals(signature)) {
            throw new IllegalArgumentException("Invalid signature");
        }
        String[] payloadParts = payload.split(":");
        if (payloadParts.length != 2) {
            throw new IllegalArgumentException("Invalid token payload");
        }
        long expiresAt = Long.parseLong(payloadParts[1]);
        if (System.currentTimeMillis() >= expiresAt) {
            throw new IllegalArgumentException("Token expired");
        }
        return payloadParts[0];
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("Token signing failed", ex);
        }
    }
}
