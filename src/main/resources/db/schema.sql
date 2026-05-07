-- Smart Home Automation — SQLite schema
-- All tables are CREATE IF NOT EXISTS so initialisation is idempotent.

CREATE TABLE IF NOT EXISTS users (
    user_id      TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    pin          TEXT NOT NULL,
    role         TEXT NOT NULL DEFAULT 'USER',
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id      TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS devices (
    device_id    TEXT PRIMARY KEY,
    name         TEXT NOT NULL,
    type         TEXT NOT NULL,                  -- LIGHT | THERMOSTAT | LOCK | CAMERA
    family       TEXT NOT NULL DEFAULT 'VERSION2', -- VERSION1 | VERSION2 (Abstract Factory family)
    room_id      TEXT NOT NULL,
    powered_on   INTEGER NOT NULL DEFAULT 0,
    state_blob   TEXT,                           -- key=value;key=value type-specific state
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE IF NOT EXISTS device_events (
    event_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id    TEXT NOT NULL,
    event_type   TEXT NOT NULL,
    timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
);

CREATE TABLE IF NOT EXISTS schedules (
    schedule_id  TEXT PRIMARY KEY,
    device_id    TEXT,
    mode_name    TEXT,
    cron_expr    TEXT NOT NULL,
    enabled      INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (device_id) REFERENCES devices(device_id)
);

CREATE TABLE IF NOT EXISTS commands_log (
    command_id   TEXT PRIMARY KEY,
    device_id    TEXT,
    action       TEXT NOT NULL,
    params_json  TEXT,
    result       TEXT NOT NULL,
    timestamp    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
