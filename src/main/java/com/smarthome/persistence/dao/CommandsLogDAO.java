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


// DAO for inserting and querying executed command history.
public class CommandsLogDAO {
    private final Connection conn;

    public CommandsLogDAO() {
        this(Database.getInstance().getConnection());
    }

    public CommandsLogDAO(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "conn must not be null");
    }

    public void insert(String commandId, String deviceId, String action,
                       String paramsJson, String result) {
        String sql = "INSERT INTO commands_log "
                   + "(command_id, device_id, action, params_json, result) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, commandId);
            stmt.setString(2, deviceId);
            stmt.setString(3, action);
            stmt.setString(4, paramsJson);
            stmt.setString(5, result);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert command log " + commandId, e);
        }
    }

    public List<CommandLog> findRecent(int limit) {
        List<CommandLog> logs = new ArrayList<>();
        String sql = "SELECT command_id, device_id, action, params_json, result, timestamp "
                   + "FROM commands_log ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load recent command logs", e);
        }
        return logs;
    }

    public List<CommandLog> findByDevice(String deviceId, int limit) {
        List<CommandLog> logs = new ArrayList<>();
        String sql = "SELECT command_id, device_id, action, params_json, result, timestamp "
                   + "FROM commands_log WHERE device_id = ? ORDER BY timestamp DESC LIMIT ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deviceId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load command logs for device " + deviceId, e);
        }
        return logs;
    }

    private CommandLog map(ResultSet rs) throws SQLException {
        return new CommandLog(
            rs.getString("command_id"),
            rs.getString("device_id"),
            rs.getString("action"),
            rs.getString("params_json"),
            rs.getString("result"),
            parseTimestamp(rs.getString("timestamp"))
        );
    }

    private Instant parseTimestamp(String raw) {
        if (raw == null) return null;
        return Instant.parse(raw.replace(' ', 'T') + "Z");
    }
}
