package com.inhouse.event;

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
 * 事件数据库仓库。
 */
@Repository
public class EventRepository {
    private static final TypeReference<Map<String, Object>> MAP_TYPE =
            new TypeReference<Map<String, Object>>() {};

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveEvent(Event event) {
        jdbcTemplate.update(
                "INSERT INTO events (id, topic, payload, created_at) VALUES (?, ?, ?, ?)",
                event.getId(),
                event.getTopic(),
                writeJson(event.getPayload()),
                toTimestamp(event.getCreatedAt()));
    }

    public List<Event> listEvents() {
        return jdbcTemplate.query("SELECT * FROM events ORDER BY created_at DESC", new EventRowMapper());
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

    private class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = new Event();
            event.setId(rs.getString("id"));
            event.setTopic(rs.getString("topic"));
            event.setPayload(readJson(rs.getString("payload")));
            event.setCreatedAt(rs.getTimestamp("created_at"));
            return event;
        }
    }
}
