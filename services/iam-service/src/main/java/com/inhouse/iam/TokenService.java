package com.inhouse.iam;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 简易 Token 服务，基于 HMAC 签名。
 */
public class TokenService {
    private static final String TOKEN_PREFIX = "iam:token:";
    private static final String USER_TOKEN_PREFIX = "iam:user-token:";
    // 签名密钥
    private static final byte[] SECRET = "inhouse-secret".getBytes(StandardCharsets.UTF_8);
    // Token 有效期
    private static final long TOKEN_TTL_MILLIS = TimeUnit.HOURS.toMillis(1);
    private final StringRedisTemplate stringRedisTemplate;

    public TokenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public TokenResponse issueToken(String userId) {
        String existingToken = stringRedisTemplate.opsForValue().get(userTokenKey(userId));
        if (existingToken != null) {
            stringRedisTemplate.delete(tokenKey(existingToken));
        }
        // 生成带过期时间的 token
        long expiresAt = System.currentTimeMillis() + TOKEN_TTL_MILLIS;
        String payload = userId + ":" + expiresAt;
        String signature = sign(payload);
        String token = Base64.getUrlEncoder().encodeToString((payload + "." + signature).getBytes(StandardCharsets.UTF_8));
        Duration ttl = Duration.ofMillis(TOKEN_TTL_MILLIS);
        stringRedisTemplate.opsForValue().set(tokenKey(token), userId, ttl);
        stringRedisTemplate.opsForValue().set(userTokenKey(userId), token, ttl);
        TokenResponse response = new TokenResponse();
        response.setAccessToken(token);
        response.setExpiresAt(new Date(expiresAt));
        return response;
    }

    public String validate(String token) {
        // 校验 token 有效性
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
        String storedUserId = stringRedisTemplate.opsForValue().get(tokenKey(token));
        if (storedUserId == null || !storedUserId.equals(payloadParts[0])) {
            throw new IllegalArgumentException("Token not found");
        }
        String currentToken = stringRedisTemplate.opsForValue().get(userTokenKey(storedUserId));
        if (currentToken == null || !currentToken.equals(token)) {
            throw new IllegalArgumentException("Token not current");
        }
        return payloadParts[0];
    }

    private String sign(String payload) {
        // 使用 HMAC SHA256 生成签名
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET, "HmacSHA256"));
            byte[] signature = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(signature);
        } catch (Exception ex) {
            throw new IllegalStateException("Token signing failed", ex);
        }
    }

    private String tokenKey(String token) {
        return TOKEN_PREFIX + token;
    }

    private String userTokenKey(String userId) {
        return USER_TOKEN_PREFIX + userId;
    }
}
