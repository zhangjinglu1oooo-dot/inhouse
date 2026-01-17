package com.inhouse.observability;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 审计日志数据库仓库。
 */
@Repository
public class AuditRepository {
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {};

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAudit(AuditEntry entry) {
        jdbcTemplate.update(
                "INSERT INTO audits (id, action, actor, target, detail, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                entry.getId(),
                entry.getAction(),
                entry.getActor(),
                entry.getTarget(),
                writeJson(entry.getDetail()),
                toTimestamp(entry.getCreatedAt()));
    }

    public List<AuditEntry> listAudits() {
        return jdbcTemplate.query("SELECT * FROM audits ORDER BY created_at DESC", new AuditRowMapper());
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

    private class AuditRowMapper implements RowMapper<AuditEntry> {
        @Override
        public AuditEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            AuditEntry entry = new AuditEntry();
            entry.setId(rs.getString("id"));
            entry.setAction(rs.getString("action"));
            entry.setActor(rs.getString("actor"));
            entry.setTarget(rs.getString("target"));
            entry.setDetail(readJson(rs.getString("detail")));
            entry.setCreatedAt(rs.getTimestamp("created_at"));
            return entry;
        }
    }
}
