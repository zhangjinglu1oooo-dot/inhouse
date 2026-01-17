package com.inhouse.iam;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Component;

/**
 * 密码加盐哈希服务。
 */
@Component
public class PasswordService {
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordHash hashPassword(String rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        byte[] hashed = pbkdf2(rawPassword, salt);
        return new PasswordHash(encode(hashed), encode(salt));
    }

    public boolean matches(String rawPassword, String encodedSalt, String encodedHash) {
        if (rawPassword == null || encodedSalt == null || encodedHash == null) {
            return false;
        }
        byte[] salt = decode(encodedSalt);
        byte[] expected = decode(encodedHash);
        byte[] actual = pbkdf2(rawPassword, salt);
        if (actual.length != expected.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < actual.length; i++) {
            result |= actual[i] ^ expected[i];
        }
        return result == 0;
    }

    private byte[] pbkdf2(String rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    rawPassword.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to hash password.", ex);
        }
    }

    private String encode(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    private byte[] decode(String value) {
        return Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
    }
}
