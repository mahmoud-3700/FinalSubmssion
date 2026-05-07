package com.smarthome.command;

import com.smarthome.devices.Thermostat;

import java.util.Objects;


// Concrete Command for thermostat setTemperature() action.
public class SetTemperatureCommand implements DeviceCommand {
    // Receiver object in Command pattern terms.
    private final Thermostat receiver;
    // Requested target value from caller.
    private final double newTemperatureC;
    // Stored old value so undo restores exact previous reading.
    private double previousTemperatureC;

    public SetTemperatureCommand(Thermostat receiver, double newTemperatureC) {
        this.receiver = Objects.requireNonNull(receiver, "receiver must not be null");
        this.newTemperatureC = newTemperatureC;
    }

    @Override
    public void execute() {
        previousTemperatureC = receiver.getTemperature();
        receiver.setTemperature(newTemperatureC);
    }

    @Override
    public void undo() {
        receiver.setTemperature(previousTemperatureC);
    }

    @Override
    public String describe() {
        return "Set " + receiver.getName() + " to " + newTemperatureC + "°C";
    }
}
