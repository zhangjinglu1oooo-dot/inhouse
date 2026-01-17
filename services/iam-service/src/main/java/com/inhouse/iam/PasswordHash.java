package com.inhouse.iam;

/**
 * 密码哈希载体。
 */
public class PasswordHash {
    private final String hash;
    private final String salt;

    public PasswordHash(String hash, String salt) {
        this.hash = hash;
        this.salt = salt;
    }

    public String getHash() {
        return hash;
    }

    public String getSalt() {
        return salt;
    }
}
