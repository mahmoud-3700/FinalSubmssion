package com.smarthome.command;

import com.smarthome.devices.Lock;

import java.util.Objects;


// Concrete Command for unlock() action on a Lock receiver.
public class UnlockCommand implements DeviceCommand {
    // Receiver object that knows how to perform the real operation.
    private final Lock receiver;
    // Captured state for undo support.
    private boolean wasLockedBefore;

    public UnlockCommand(Lock receiver) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
    }

    @Override
    public void execute() {
        wasLockedBefore = receiver.isLocked();
        receiver.unlock();
    }

    @Override
    public void undo() {
        if (wasLockedBefore) {
            receiver.lock();
        }
    }

    @Override
    public String describe() {
        return "Unlock " + receiver.getName();
    }
}
