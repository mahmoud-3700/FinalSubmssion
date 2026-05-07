package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// Concrete Decorator: adds action logging without changing Device classes.
public class LoggingDeviceDecorator extends DeviceDecorator {

    private final List<String> log = new ArrayList<>();

    public LoggingDeviceDecorator(Device wrappee) {
        super(wrappee);
    }

    @Override
    public void turnOn() {
        // Extra behavior is added before forwarding to wrapped object.
        log.add("turnOn(" + wrappee.getName() + ")");
        super.turnOn();
    }

    @Override
    public void turnOff() {
        log.add("turnOff(" + wrappee.getName() + ")");
        super.turnOff();
    }

    // Expose logs as read-only so callers cannot corrupt decorator state.
    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }
}
