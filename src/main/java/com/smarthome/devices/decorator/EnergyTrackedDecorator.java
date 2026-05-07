package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;


// Concrete Decorator: adds on-time tracking to any wrapped device.
public class EnergyTrackedDecorator extends DeviceDecorator {

    private long onSinceMillis = 0L;
    private long totalOnMillis = 0L;

    public EnergyTrackedDecorator(Device wrappee) {
        super(wrappee);
    }

    @Override
    public void turnOn() {
        // Capture timestamp only for real off -> on transitions.
        boolean wasOff = !wrappee.isPoweredOn();
        super.turnOn();
        if (wasOff && wrappee.isPoweredOn()) {
            onSinceMillis = System.currentTimeMillis();
        }
    }

    @Override
    public void turnOff() {
        if (wrappee.isPoweredOn()) {
            totalOnMillis += System.currentTimeMillis() - onSinceMillis;
        }
        super.turnOff();
    }

    // Includes current live session when device is still on.
    public long getTotalOnMillis() {
        long live = wrappee.isPoweredOn()
            ? System.currentTimeMillis() - onSinceMillis
            : 0L;
        return totalOnMillis + live;
    }
}
