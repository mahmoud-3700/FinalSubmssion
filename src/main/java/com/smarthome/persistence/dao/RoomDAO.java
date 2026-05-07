package com.smarthome.persistence.dao;

import com.smarthome.core.Room;
import com.smarthome.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// DAO for creating and listing room records.
public class RoomDAO {
    private final Connection conn;

    public RoomDAO() {
        this(Database.getInstance().getConnection());
    }

    public RoomDAO(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "conn must not be null");
    }

    public void insert(Room room) {
        Objects.requireNonNull(room, "room must not be null");
        String sql = "INSERT INTO rooms (room_id, name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomId());
            stmt.setString(2, room.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert room " + room.getRoomId(), e);
        }
    }

    public Room findById(String roomId) {
        String sql = "SELECT room_id, name FROM rooms WHERE room_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(rs.getString("room_id"), rs.getString("name"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find room " + roomId, e);
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, name FROM rooms ORDER BY name";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(new Room(rs.getString("room_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load rooms", e);
        }
        return rooms;
    }

    public void delete(String roomId) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete room " + roomId, e);
        }
    }
}
