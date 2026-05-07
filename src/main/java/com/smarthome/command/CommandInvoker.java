package com.smarthome.command;

import com.smarthome.persistence.dao.CommandsLogDAO;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


// Command pattern Invoker: triggers commands and manages undo history.
public class CommandInvoker {
    // LIFO stack so undo always affects the most recent action first.
    private final Deque<DeviceCommand> history = new ArrayDeque<>();
    // Optional DAO used to persist command audit rows.
    private final CommandsLogDAO auditLog;

    // Lightweight mode used by tests or in-memory runs.
    public CommandInvoker() {
        this(null);
    }

    public CommandInvoker(CommandsLogDAO auditLog) {
        this.auditLog = auditLog;
    }

    // Invoker runs the command but never touches device internals directly.
    public void execute(DeviceCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        command.execute();
        history.push(command);
        if (auditLog != null) {
            auditLog.insert(
                UUID.randomUUID().toString(),
                null,                       // device_id resolution belongs in commands; keep null here
                command.describe(),
                "{}",
                "OK"
            );
        }
    }

    // Used by UI to enable/disable Undo button.
    public boolean canUndo() {
        return !history.isEmpty();
    }

    // Pops one command and asks that command to reverse itself.
    public DeviceCommand undo() {
        if (history.isEmpty()) {
            return null;
        }
        DeviceCommand last = history.pop();
        last.undo();
        return last;
    }

    public List<DeviceCommand> getHistory() {
        return Collections.unmodifiableList(List.copyOf(history));
    }

    public void clearHistory() {
        history.clear();
    }
}
