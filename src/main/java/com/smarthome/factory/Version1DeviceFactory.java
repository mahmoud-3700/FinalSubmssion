package com.smarthome.factory;

import com.smarthome.devices.Device;
import com.smarthome.devices.version1.Version1Camera;
import com.smarthome.devices.version1.Version1Light;
import com.smarthome.devices.version1.Version1Lock;
import com.smarthome.devices.version1.Version1Thermostat;


// Concrete factory that creates Version1 device variants.
public class Version1DeviceFactory extends DeviceFactory {
    @Override
    public Device createLight(String name) {
        return new Version1Light(newId(), name);
    }

    @Override
    public Device createThermostat(String name) {
        return new Version1Thermostat(newId(), name);
    }

    @Override
    public Device createDoorLock(String name) {
        return new Version1Lock(newId(), name);
    }

    @Override
    public Device createCamera(String name) {
        return new Version1Camera(newId(), name);
    }
}
