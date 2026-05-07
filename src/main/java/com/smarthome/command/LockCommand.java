package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;


// Concrete Command for lock() action on a Lock receiver.
public class LockCommand implements DeviceCommand {
    // Receiver object that knows how to perform the real operation.
    private final Lock receiver;
    // Captured state for undo support.
    private boolean wasLockedBefore;

    public LockCommand(Lock receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        // Save previous state before mutation.
        wasLockedBefore = receiver.isLocked();
        receiver.lock();
    }

    @Override
    public void undo() {
        if (!wasLockedBefore) {
            receiver.unlock();
        }
    }

    @Override
    public String describe() {
        return "Lock " + receiver.getName();
    }
}
