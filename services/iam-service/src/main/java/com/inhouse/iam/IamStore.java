package com.inhouse.iam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IAM 内存存储。
 */
public class IamStore {
    // 用户集合
    private final Map<String, User> users = new ConcurrentHashMap<String, User>();
    // 角色集合
    private final Map<String, Role> roles = new ConcurrentHashMap<String, Role>();

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Role> getRoles() {
        return roles;
    }
}
