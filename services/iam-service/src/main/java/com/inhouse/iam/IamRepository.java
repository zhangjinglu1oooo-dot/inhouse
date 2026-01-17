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
}
