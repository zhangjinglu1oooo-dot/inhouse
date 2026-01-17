package com.inhouse.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * IAM 数据库仓库。
 */
@Repository
public class IamRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO iam_users (id, employee_id, username, password_hash, password_salt, display_name, email, phone, status, avatar_url, last_login_at, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getEmployeeId(),
                user.getUsername(),
                user.getPassword(),
                user.getPasswordSalt(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getAvatarUrl(),
                toTimestamp(user.getLastLoginAt()),
                toTimestamp(user.getCreatedAt()),
                toTimestamp(user.getUpdatedAt()));

        jdbcTemplate.update(
                "INSERT INTO iam_user_profiles (user_id, department_id, title, manager_id, location, hire_date) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getDepartmentId(),
                user.getTitle(),
                user.getManagerId(),
                user.getLocation(),
                toSqlDate(user.getHireDate()));

        insertUserAttributes(user);
        insertUserRoles(user);
    }

    public List<User> listUsers() {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, p.department_id, p.title, p.manager_id, p.location, p.hire_date "
                        + "FROM iam_users u "
                        + "LEFT JOIN iam_user_profiles p ON u.id = p.user_id "
                        + "ORDER BY u.created_at DESC",
                new UserRowMapper());
        users.forEach(this::loadUserRelations);
        return users;
    }

    public List<User> listUsersPage(int offset, int limit) {
        List<User> users = jdbcTemplate.query(
                "SELECT u.*, p.department_id, p.title, p.manager_id, p.location, p.hire_date "
                        + "FROM iam_users u "
                        + "LEFT JOIN iam_user_profiles p ON u.id = p.user_id "
                        + "ORDER BY u.created_at DESC "
                        + "LIMIT ? OFFSET ?",
                new UserRowMapper(),
                limit,
                offset);
        users.forEach(this::loadUserRelations);
        return users;
    }

    public int countUsers() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM iam_users",
                Integer.class);
        return count == null ? 0 : count;
    }

    public Optional<User> findUserByAccount(String account) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT u.*, p.department_id, p.title, p.manager_id, p.location, p.hire_date "
                            + "FROM iam_users u "
                            + "LEFT JOIN iam_user_profiles p ON u.id = p.user_id "
                            + "WHERE u.username = ?",
                    new UserRowMapper(),
                    account);
            if (user != null) {
                loadUserRelations(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<User> findUserById(String userId) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT u.*, p.department_id, p.title, p.manager_id, p.location, p.hire_date "
                            + "FROM iam_users u "
                            + "LEFT JOIN iam_user_profiles p ON u.id = p.user_id "
                            + "WHERE u.id = ?",
                    new UserRowMapper(),
                    userId);
            if (user != null) {
                loadUserRelations(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE iam_users SET employee_id = ?, username = ?, password_hash = ?, password_salt = ?, "
                        + "display_name = ?, email = ?, phone = ?, status = ?, avatar_url = ?, last_login_at = ?, "
                        + "updated_at = ? WHERE id = ?",
                user.getEmployeeId(),
                user.getUsername(),
                user.getPassword(),
                user.getPasswordSalt(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getAvatarUrl(),
                toTimestamp(user.getLastLoginAt()),
                toTimestamp(user.getUpdatedAt()),
                user.getId());

        int updated = jdbcTemplate.update(
                "UPDATE iam_user_profiles SET department_id = ?, title = ?, manager_id = ?, location = ?, hire_date = ? "
                        + "WHERE user_id = ?",
                user.getDepartmentId(),
                user.getTitle(),
                user.getManagerId(),
                user.getLocation(),
                toSqlDate(user.getHireDate()),
                user.getId());
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO iam_user_profiles (user_id, department_id, title, manager_id, location, hire_date) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    user.getId(),
                    user.getDepartmentId(),
                    user.getTitle(),
                    user.getManagerId(),
                    user.getLocation(),
                    toSqlDate(user.getHireDate()));
        }
    }

    public void deleteUser(String userId) {
        jdbcTemplate.update("DELETE FROM iam_user_roles WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM iam_user_attributes WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM iam_user_profiles WHERE user_id = ?", userId);
        jdbcTemplate.update("DELETE FROM iam_users WHERE id = ?", userId);
    }

    public void replaceUserRoles(String userId, List<String> roleIds) {
        jdbcTemplate.update("DELETE FROM iam_user_roles WHERE user_id = ?", userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        Timestamp createdAt = toTimestamp(new java.util.Date());
        for (String roleId : roleIds) {
            jdbcTemplate.update(
                    "INSERT INTO iam_user_roles (user_id, role_id, created_at) VALUES (?, ?, ?)",
                    userId,
                    roleId,
                    createdAt);
        }
    }

    public void replaceUserAttributes(String userId, Map<String, Object> attributes) {
        jdbcTemplate.update("DELETE FROM iam_user_attributes WHERE user_id = ?", userId);
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        Timestamp createdAt = toTimestamp(new java.util.Date());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            jdbcTemplate.update(
                    "INSERT INTO iam_user_attributes (user_id, attr_key, attr_value, created_at) VALUES (?, ?, ?, ?)",
                    userId,
                    entry.getKey(),
                    writeJsonValue(entry.getValue()),
                    createdAt);
        }
    }

    public void saveRole(Role role) {
        jdbcTemplate.update(
                "INSERT INTO iam_roles (id, name, description, created_at) VALUES (?, ?, ?, ?)",
                role.getId(),
                role.getName(),
                role.getDescription(),
                toTimestamp(role.getCreatedAt()));

        if (role.getPermissions() != null) {
            for (RolePermission permission : role.getPermissions()) {
                String permissionId = ensurePermission(permission);
                jdbcTemplate.update(
                        "INSERT INTO iam_role_permissions (role_id, permission_id, effect, created_at) "
                                + "VALUES (?, ?, ?, ?)",
                        role.getId(),
                        permissionId,
                        permission.getEffect(),
                        toTimestamp(role.getCreatedAt()));
            }
        }
    }

    public List<Role> listRoles() {
        List<Role> roles = jdbcTemplate.query(
                "SELECT * FROM iam_roles ORDER BY created_at DESC",
                new RoleRowMapper());
        roles.forEach(this::loadRolePermissions);
        return roles;
    }

    public List<Role> listRolesPage(int offset, int limit) {
        List<Role> roles = jdbcTemplate.query(
                "SELECT * FROM iam_roles ORDER BY created_at DESC LIMIT ? OFFSET ?",
                new RoleRowMapper(),
                limit,
                offset);
        roles.forEach(this::loadRolePermissions);
        return roles;
    }

    public int countRoles() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM iam_roles",
                Integer.class);
        return count == null ? 0 : count;
    }

    public Optional<Role> findRoleById(String roleId) {
        try {
            Role role = jdbcTemplate.queryForObject(
                    "SELECT * FROM iam_roles WHERE id = ?",
                    new RoleRowMapper(),
                    roleId);
            if (role != null) {
                loadRolePermissions(role);
            }
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void updateRole(Role role) {
        jdbcTemplate.update(
                "UPDATE iam_roles SET name = ?, description = ? WHERE id = ?",
                role.getName(),
                role.getDescription(),
                role.getId());
        if (role.getPermissions() != null) {
            jdbcTemplate.update("DELETE FROM iam_role_permissions WHERE role_id = ?", role.getId());
            Timestamp createdAt = toTimestamp(new java.util.Date());
            for (RolePermission permission : role.getPermissions()) {
                String permissionId = ensurePermission(permission);
                jdbcTemplate.update(
                        "INSERT INTO iam_role_permissions (role_id, permission_id, effect, created_at) "
                                + "VALUES (?, ?, ?, ?)",
                        role.getId(),
                        permissionId,
                        permission.getEffect(),
                        createdAt);
            }
        }
    }

    public void deleteRole(String roleId) {
        jdbcTemplate.update("DELETE FROM iam_user_roles WHERE role_id = ?", roleId);
        jdbcTemplate.update("DELETE FROM iam_role_permissions WHERE role_id = ?", roleId);
        jdbcTemplate.update("DELETE FROM iam_roles WHERE id = ?", roleId);
    }

    public void savePermission(Permission permission) {
        jdbcTemplate.update(
                "INSERT INTO iam_permissions (id, app, feature, resource, description, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                permission.getId(),
                permission.getApp(),
                permission.getFeature(),
                permission.getResource(),
                permission.getDescription(),
                toTimestamp(permission.getCreatedAt()));
    }

    public List<Permission> listPermissions() {
        return jdbcTemplate.query(
                "SELECT * FROM iam_permissions ORDER BY created_at DESC",
                new PermissionRowMapper());
    }

    public List<Permission> listPermissionsPage(int offset, int limit) {
        return jdbcTemplate.query(
                "SELECT * FROM iam_permissions ORDER BY created_at DESC LIMIT ? OFFSET ?",
                new PermissionRowMapper(),
                limit,
                offset);
    }

    public int countPermissions() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM iam_permissions",
                Integer.class);
        return count == null ? 0 : count;
    }

    public Optional<Permission> findPermissionById(String permissionId) {
        try {
            Permission permission = jdbcTemplate.queryForObject(
                    "SELECT * FROM iam_permissions WHERE id = ?",
                    new PermissionRowMapper(),
                    permissionId);
            return Optional.ofNullable(permission);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void updatePermission(Permission permission) {
        jdbcTemplate.update(
                "UPDATE iam_permissions SET app = ?, feature = ?, resource = ?, description = ? WHERE id = ?",
                permission.getApp(),
                permission.getFeature(),
                permission.getResource(),
                permission.getDescription(),
                permission.getId());
    }

    public void deletePermission(String permissionId) {
        jdbcTemplate.update("DELETE FROM iam_role_permissions WHERE permission_id = ?", permissionId);
        jdbcTemplate.update("DELETE FROM iam_permissions WHERE id = ?", permissionId);
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    private java.sql.Date toSqlDate(java.util.Date date) {
        return date == null ? null : new java.sql.Date(date.getTime());
    }

    private String writeJsonValue(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize json.", ex);
        }
    }

    private Object readJsonValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(value, Object.class);
        } catch (Exception ex) {
            return value;
        }
    }

    private void insertUserAttributes(User user) {
        if (user.getAttributes() == null || user.getAttributes().isEmpty()) {
            return;
        }
        Timestamp createdAt = toTimestamp(user.getCreatedAt());
        for (Map.Entry<String, Object> entry : user.getAttributes().entrySet()) {
            jdbcTemplate.update(
                    "INSERT INTO iam_user_attributes (user_id, attr_key, attr_value, created_at) VALUES (?, ?, ?, ?)",
                    user.getId(),
                    entry.getKey(),
                    writeJsonValue(entry.getValue()),
                    createdAt);
        }
    }

    private void insertUserRoles(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return;
        }
        Timestamp createdAt = toTimestamp(user.getCreatedAt());
        for (String roleId : user.getRoles()) {
            jdbcTemplate.update(
                    "INSERT INTO iam_user_roles (user_id, role_id, created_at) VALUES (?, ?, ?)",
                    user.getId(),
                    roleId,
                    createdAt);
        }
    }

    private void loadUserRelations(User user) {
        user.setRoles(loadUserRoleIds(user.getId()));
        user.setAttributes(loadUserAttributes(user.getId()));
    }

    private List<String> loadUserRoleIds(String userId) {
        return jdbcTemplate.query(
                "SELECT role_id FROM iam_user_roles WHERE user_id = ? ORDER BY created_at DESC",
                (rs, rowNum) -> rs.getString("role_id"),
                userId);
    }

    private Map<String, Object> loadUserAttributes(String userId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT attr_key, attr_value FROM iam_user_attributes WHERE user_id = ?",
                userId);
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (Map<String, Object> row : rows) {
            String key = (String) row.get("attr_key");
            String value = (String) row.get("attr_value");
            attributes.put(key, readJsonValue(value));
        }
        return attributes;
    }

    private String ensurePermission(RolePermission permission) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id FROM iam_permissions WHERE app = ? AND feature = ? AND resource <=> ?",
                    String.class,
                    permission.getApp(),
                    permission.getFeature(),
                    permission.getResource());
        } catch (EmptyResultDataAccessException ex) {
            String id = UUID.randomUUID().toString();
            jdbcTemplate.update(
                    "INSERT INTO iam_permissions (id, app, feature, resource, description, created_at) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    id,
                    permission.getApp(),
                    permission.getFeature(),
                    permission.getResource(),
                    null,
                    toTimestamp(new java.util.Date()));
            return id;
        }
    }

    private void loadRolePermissions(Role role) {
        List<RolePermission> permissions = jdbcTemplate.query(
                "SELECT p.app, p.feature, p.resource, rp.effect "
                        + "FROM iam_role_permissions rp "
                        + "JOIN iam_permissions p ON rp.permission_id = p.id "
                        + "WHERE rp.role_id = ? "
                        + "ORDER BY p.app, p.feature",
                new RolePermissionRowMapper(),
                role.getId());
        role.setPermissions(permissions);
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setEmployeeId(rs.getString("employee_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password_hash"));
            user.setPasswordSalt(rs.getString("password_salt"));
            user.setDisplayName(rs.getString("display_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setTitle(rs.getString("title"));
            user.setManagerId(rs.getString("manager_id"));
            user.setLocation(rs.getString("location"));
            user.setStatus(rs.getString("status"));
            user.setHireDate(rs.getDate("hire_date"));
            user.setLastLoginAt(rs.getTimestamp("last_login_at"));
            user.setAvatarUrl(rs.getString("avatar_url"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setUpdatedAt(rs.getTimestamp("updated_at"));
            user.setDepartmentId(rs.getString("department_id"));
            return user;
        }
    }

    private class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getString("id"));
            role.setName(rs.getString("name"));
            role.setDescription(rs.getString("description"));
            role.setCreatedAt(rs.getTimestamp("created_at"));
            return role;
        }
    }

    private class RolePermissionRowMapper implements RowMapper<RolePermission> {
        @Override
        public RolePermission mapRow(ResultSet rs, int rowNum) throws SQLException {
            RolePermission permission = new RolePermission();
            permission.setApp(rs.getString("app"));
            permission.setFeature(rs.getString("feature"));
            permission.setResource(rs.getString("resource"));
            permission.setEffect(rs.getString("effect"));
            return permission;
        }
    }

    private class PermissionRowMapper implements RowMapper<Permission> {
        @Override
        public Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
            Permission permission = new Permission();
            permission.setId(rs.getString("id"));
            permission.setApp(rs.getString("app"));
            permission.setFeature(rs.getString("feature"));
            permission.setResource(rs.getString("resource"));
            permission.setDescription(rs.getString("description"));
            permission.setCreatedAt(rs.getTimestamp("created_at"));
            return permission;
        }
    }
}
