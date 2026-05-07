package com.smarthome.devices.decorator;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;

import java.util.Objects;

// Base Decorator: keeps the same Device API while wrapping another Device.
public abstract class DeviceDecorator extends Device {

    // Wrapped device that receives the delegated calls.
    protected final Device wrappee;

    protected DeviceDecorator(Device wrappee) {
        // Preserve id/name so decorated objects still map to the same logical device.
        super(wrappee.getId(), wrappee.getName());
        this.wrappee = Objects.requireNonNull(wrappee, "wrappee must not be null");
    }

    @Override
    public void turnOn() {
        wrappee.turnOn();
    }

    @Override
    public void turnOff() {
        wrappee.turnOff();
    }

    @Override
    public boolean isPoweredOn() {
        return wrappee.isPoweredOn();
    }

    @Override
    public void attach(Observer observer) {
        wrappee.attach(observer);
    }

    @Override
    public void detach(Observer observer) {
        wrappee.detach(observer);
    }

    @Override
    public void notifyObservers(String event) {
        wrappee.notifyObservers(event);
    }
}
