package com.smarthome.persistence.dao;

import com.smarthome.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// DAO for inserting and querying device event audit history.
public class DeviceEventDAO {
    private final Connection conn;

    public DeviceEventDAO() {
        this(Database.getInstance().getConnection());
    }

    public DeviceEventDAO(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "conn must not be null");
    }

    public void insert(String deviceId, String eventType) {
        String sql = "INSERT INTO device_events (device_id, event_type) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceId);
            stmt.setString(2, eventType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert device event", e);
        }
    }

    public List<DeviceEvent> findRecent(int limit) {
        List<DeviceEvent> events = new ArrayList<>();
        String sql = "SELECT event_id, device_id, event_type, timestamp "
                   + "FROM device_events ORDER BY event_id DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(new DeviceEvent(
                        rs.getLong("event_id"),
                        rs.getString("device_id"),
                        rs.getString("event_type"),
                        parseTimestamp(rs.getString("timestamp"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load recent events", e);
        }
        return events;
    }

    public List<DeviceEvent> findByDevice(String deviceId, int limit) {
        List<DeviceEvent> events = new ArrayList<>();
        String sql = "SELECT event_id, device_id, event_type, timestamp "
                   + "FROM device_events WHERE device_id = ? ORDER BY event_id DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    events.add(new DeviceEvent(
                        rs.getLong("event_id"),
                        rs.getString("device_id"),
                        rs.getString("event_type"),
                        parseTimestamp(rs.getString("timestamp"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load events for device " + deviceId, e);
        }
        return events;
    }

    
    private Instant parseTimestamp(String raw) {
        if (raw == null) return null;
        return Instant.parse(raw.replace(' ', 'T') + "Z");
    }
}
