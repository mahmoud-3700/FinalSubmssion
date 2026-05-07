package com.smarthome.command;

import com.smarthome.devices.Device;

import java.util.Objects;


// Concrete Command: wraps "turn off" as an object.
public class TurnOffCommand implements DeviceCommand {
    // Receiver in Command pattern terms.
    private final Device receiver;
    // Captured pre-state for accurate undo behavior.
    private boolean wasPoweredOnBefore;

    public TurnOffCommand(Device receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasPoweredOnBefore = receiver.isPoweredOn();
        receiver.turnOff();
    }

    @Override
    public void undo() {
        if (wasPoweredOnBefore) {
            receiver.turnOn();
        }
    }

    @Override
    public String describe() {
        return "Turn off " + receiver.getName();
    }
}
