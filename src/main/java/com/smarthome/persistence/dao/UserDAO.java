package com.smarthome.persistence.dao;

import com.smarthome.persistence.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


// DAO for user lookup and authentication queries.
public class UserDAO {
    private final Connection conn;

    
    public UserDAO() {
        this(Database.getInstance().getConnection());
    }

    
    public UserDAO(Connection conn) {
        this.conn = Objects.requireNonNull(conn, "conn must not be null");
    }

    public void insert(User user) {
        Objects.requireNonNull(user, "user must not be null");
        String sql = "INSERT INTO users (user_id, name, pin, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.userId());
            stmt.setString(2, user.name());
            stmt.setString(3, user.pin());
            stmt.setString(4, user.role());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user " + user.userId(), e);
        }
    }

    public User findById(String userId) {
        String sql = "SELECT user_id, name, pin, role FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("pin"),
                        rs.getString("role")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user " + userId, e);
        }
    }

    public boolean verifyPin(String userId, String pin) {
        User user = findById(userId);
        return user != null && user.pin().equals(pin);
    }
}
