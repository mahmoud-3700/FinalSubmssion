package com.smarthome.command;


// Command pattern contract: every action must execute and be undoable.
public interface DeviceCommand {
    // Runs the action on its receiver.
    void execute();

    // Restores the receiver state captured during execute().
    void undo();

    // Readable summary used in history/audit views.
    String describe();
}
