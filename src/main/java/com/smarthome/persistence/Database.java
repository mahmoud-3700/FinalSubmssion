package com.smarthome.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

// SINGLETON pattern: holds the one SQLite JDBC connection for the application.
// Also initialises schema and runs idempotent migrations on first use.
public final class Database {
    private static final String PRODUCTION_URL = "jdbc:sqlite:smarthome.db";
    private static final Database INSTANCE = new Database(PRODUCTION_URL);

    private final Connection connection;

    private Database(String jdbcUrl) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl);
            initSchema();
            runMigrations();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite database at " + jdbcUrl, e);
        }
    }

    // Returns the production singleton wired to smarthome.db.
    public static Database getInstance() {
        return INSTANCE;
    }

    // Helper for tests that need an isolated database URL.
    public static Database forUrl(String jdbcUrl) {
        return new Database(jdbcUrl);
    }

    public Connection getConnection() {
        return connection;
    }

    private void initSchema() throws Exception {
        try (var in = getClass().getResourceAsStream("/db/schema.sql");
             var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String sql = reader.lines()
                .map(line -> {
                    int commentStart = line.indexOf("--");
                    return commentStart >= 0 ? line.substring(0, commentStart) : line;
                })
                .collect(Collectors.joining("\n"));
            try (Statement stmt = connection.createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
        }
    }

    // Applies additive migrations; safe to run multiple times.
    private void runMigrations() {
        tryMigrate(
            "ALTER TABLE devices ADD COLUMN family TEXT NOT NULL DEFAULT 'VERSION2'");

        tryMigrate(
            "ALTER TABLE devices ADD COLUMN state_blob TEXT");
    }

    private void tryMigrate(String alterSql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(alterSql);
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (!msg.contains("duplicate column") && !msg.contains("no such table")) {
                System.err.println("Migration warning: " + msg);
            }
        }
    }
}
