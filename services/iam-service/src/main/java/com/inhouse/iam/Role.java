package com.inhouse.iam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色模型。
 */
public class Role {
    // 角色 ID
    private String id;
    // 角色名称
    private String name;
    // 权限列表
    private List<RolePermission> permissions = new ArrayList<RolePermission>();
    // 创建时间
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermission> permissions) {
        this.permissions = permissions;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
