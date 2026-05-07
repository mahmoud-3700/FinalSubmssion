package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;


// Concrete Command: wraps "turn on" as an object.
public class TurnOnCommand implements DeviceCommand {
    // Receiver in Command pattern terms.
    private final Device receiver;
    // Captured pre-state for accurate undo behavior.
    private boolean wasPoweredOnBefore;

    public TurnOnCommand(Device receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        // Capture state before mutating so undo is deterministic.
        wasPoweredOnBefore = receiver.isPoweredOn();
        receiver.turnOn();
    }

    @Override
    public void undo() {
        if (!wasPoweredOnBefore) {
            receiver.turnOff();
        }
    }

    @Override
    public String describe() {
        return "Turn on " + receiver.getName();
    }
}
