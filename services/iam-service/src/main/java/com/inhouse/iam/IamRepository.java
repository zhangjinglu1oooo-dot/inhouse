package com.inhouse.iam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * IAM 数据库仓库。
 */
@Repository
public class IamRepository {
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<List<String>>() {};
    private static final TypeReference<List<RolePermission>> PERMISSION_LIST =
            new TypeReference<List<RolePermission>>() {};
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {};

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO iam_users (id, employee_id, username, password, display_name, email, phone, department, title, manager_id, location, status, hire_date, last_login_at, avatar_url, roles, attributes, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getId(),
                user.getEmployeeId(),
                user.getUsername(),
                user.getPassword(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhone(),
                user.getDepartment(),
                user.getTitle(),
                user.getManagerId(),
                user.getLocation(),
                user.getStatus(),
                toSqlDate(user.getHireDate()),
                toTimestamp(user.getLastLoginAt()),
                user.getAvatarUrl(),
                writeJson(user.getRoles()),
                writeJson(user.getAttributes()),
                toTimestamp(user.getCreatedAt()));
    }

    public List<User> listUsers() {
        return jdbcTemplate.query("SELECT * FROM iam_users ORDER BY created_at DESC", new UserRowMapper());
    }

    public Optional<User> findUserByUsernameAndPassword(String username, String password) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM iam_users WHERE username = ? AND password = ?",
                    new UserRowMapper(),
                    username,
                    password);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void saveRole(Role role) {
        jdbcTemplate.update(
                "INSERT INTO iam_roles (id, name, permissions, created_at) VALUES (?, ?, ?, ?)",
                role.getId(),
                role.getName(),
                writeJson(role.getPermissions()),
                toTimestamp(role.getCreatedAt()));
    }

    public List<Role> listRoles() {
        return jdbcTemplate.query("SELECT * FROM iam_roles ORDER BY created_at DESC", new RoleRowMapper());
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    private Date toSqlDate(java.util.Date date) {
        return date == null ? null : new Date(date.getTime());
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize json.", ex);
        }
    }

    private <T> T readJson(String value, TypeReference<T> type, T fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return objectMapper.readValue(value, type);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize json.", ex);
        }
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setEmployeeId(rs.getString("employee_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setDisplayName(rs.getString("display_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setDepartment(rs.getString("department"));
            user.setTitle(rs.getString("title"));
            user.setManagerId(rs.getString("manager_id"));
            user.setLocation(rs.getString("location"));
            user.setStatus(rs.getString("status"));
            user.setHireDate(rs.getDate("hire_date"));
            user.setLastLoginAt(rs.getTimestamp("last_login_at"));
            user.setAvatarUrl(rs.getString("avatar_url"));
            user.setRoles(readJson(rs.getString("roles"), STRING_LIST, new ArrayList<String>()));
            user.setAttributes(readJson(rs.getString("attributes"), MAP_TYPE, new java.util.HashMap<String, Object>()));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            return user;
        }
    }

    private class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getString("id"));
            role.setName(rs.getString("name"));
            role.setPermissions(readJson(rs.getString("permissions"), PERMISSION_LIST, new ArrayList<RolePermission>()));
            role.setCreatedAt(rs.getTimestamp("created_at"));
            return role;
        }
    }
}
