package com.inhouse.permission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 权限策略数据库仓库。
 */
@Repository
public class PolicyRepository {
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {};

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PolicyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void savePolicy(Policy policy) {
        jdbcTemplate.update(
                "INSERT INTO permission_policies (id, app, feature, resource, effect, conditions, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                policy.getId(),
                policy.getApp(),
                policy.getFeature(),
                policy.getResource(),
                policy.getEffect(),
                writeJson(policy.getConditions()),
                toTimestamp(policy.getCreatedAt()));
    }

    public List<Policy> listPolicies() {
        return jdbcTemplate.query(
                "SELECT * FROM permission_policies ORDER BY created_at DESC", new PolicyRowMapper());
    }

    private Timestamp toTimestamp(java.util.Date date) {
        return date == null ? null : new Timestamp(date.getTime());
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

    private Map<String, Object> readJson(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new HashMap<String, Object>();
        }
        try {
            return objectMapper.readValue(value, MAP_TYPE);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize json.", ex);
        }
    }

    private class PolicyRowMapper implements RowMapper<Policy> {
        @Override
        public Policy mapRow(ResultSet rs, int rowNum) throws SQLException {
            Policy policy = new Policy();
            policy.setId(rs.getString("id"));
            policy.setApp(rs.getString("app"));
            policy.setFeature(rs.getString("feature"));
            policy.setResource(rs.getString("resource"));
            policy.setEffect(rs.getString("effect"));
            policy.setConditions(readJson(rs.getString("conditions")));
            policy.setCreatedAt(rs.getTimestamp("created_at"));
            return policy;
        }
    }
}
