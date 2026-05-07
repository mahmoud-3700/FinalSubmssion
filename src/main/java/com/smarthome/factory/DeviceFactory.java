package com.smarthome.factory;

import com.smarthome.devices.Device;
import java.util.UUID;

// Abstract Factory: each subclass creates a consistent device family.
public abstract class DeviceFactory {
    // Factory Method for light variants.
    public abstract Device createLight(String name);

    // Factory Method for thermostat variants.
    public abstract Device createThermostat(String name);

    // Factory Method for lock variants.
    public abstract Device createDoorLock(String name);

    // Factory Method for camera variants.
    public abstract Device createCamera(String name);

    // Shared id generator used by all concrete factories.
    protected String newId() {
        return UUID.randomUUID().toString();
    }
}
