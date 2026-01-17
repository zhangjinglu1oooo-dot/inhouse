package com.inhouse.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 应用注册数据库仓库。
 */
@Repository
public class AppRepository {
    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<List<String>>() {};

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AppRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveApp(AppDefinition app) {
        jdbcTemplate.update(
                "INSERT INTO registry_apps (id, name, version, status, entry_url, description, features, tags, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                app.getId(),
                app.getName(),
                app.getVersion(),
                app.getStatus(),
                app.getEntryUrl(),
                app.getDescription(),
                writeJson(app.getFeatures()),
                writeJson(app.getTags()),
                toTimestamp(app.getCreatedAt()));
    }

    public List<AppDefinition> listApps() {
        return jdbcTemplate.query("SELECT * FROM registry_apps ORDER BY created_at DESC", new AppRowMapper());
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

    private List<String> readJson(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new ArrayList<String>();
        }
        try {
            return objectMapper.readValue(value, STRING_LIST);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize json.", ex);
        }
    }

    private class AppRowMapper implements RowMapper<AppDefinition> {
        @Override
        public AppDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
            AppDefinition app = new AppDefinition();
            app.setId(rs.getString("id"));
            app.setName(rs.getString("name"));
            app.setVersion(rs.getString("version"));
            app.setStatus(rs.getString("status"));
            app.setEntryUrl(rs.getString("entry_url"));
            app.setDescription(rs.getString("description"));
            app.setFeatures(readJson(rs.getString("features")));
            app.setTags(readJson(rs.getString("tags")));
            app.setCreatedAt(rs.getTimestamp("created_at"));
            return app;
        }
    }
}
