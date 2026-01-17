package com.inhouse.iam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IamStore {
    private final Map<String, User> users = new ConcurrentHashMap<String, User>();
    private final Map<String, Role> roles = new ConcurrentHashMap<String, Role>();

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, Role> getRoles() {
        return roles;
    }
}
