package com.smarthome.command;

import com.smarthome.core.SmartHomeHub;
import com.smarthome.strategy.AutomationMode;

import java.util.Objects;


// Concrete Command: changes the hub's active Strategy.
public class SetAutomationModeCommand implements DeviceCommand {
    // Receiver is the Strategy context (SmartHomeHub).
    private final SmartHomeHub receiver;
    // Strategy instance to apply.
    private final AutomationMode newMode;
    // Stored so undo can restore the previous strategy.
    private AutomationMode previousMode;

    public SetAutomationModeCommand(SmartHomeHub receiver, AutomationMode newMode) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
        this.newMode = Objects.requireNonNull(newMode, "newMode must not be null");
    }

    @Override
    public void execute() {
        previousMode = receiver.getAutomationMode();
        receiver.setAutomationMode(newMode);
        // Delegate strategy-specific behavior to the mode itself.
        receiver.applyAutomationMode();
    }

    @Override
    public void undo() {
        receiver.setAutomationMode(previousMode);
        receiver.applyAutomationMode();
    }

    @Override
    public String describe() {
        return "Set automation mode to " + newMode.name();
    }
}
