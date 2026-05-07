package com.smarthome.factory;

import com.smarthome.devices.Device;
import com.smarthome.devices.version2.Version2Camera;
import com.smarthome.devices.version2.Version2Light;
import com.smarthome.devices.version2.Version2Lock;
import com.smarthome.devices.version2.Version2Thermostat;


// Concrete factory that creates Version2 device variants.
public class Version2DeviceFactory extends DeviceFactory {
    @Override
    public Device createLight(String name) {
        return new Version2Light(newId(), name);
    }

    @Override
    public Device createThermostat(String name) {
        return new Version2Thermostat(newId(), name);
    }

    @Override
    public Device createDoorLock(String name) {
        return new Version2Lock(newId(), name);
    }

    @Override
    public Device createCamera(String name) {
        return new Version2Camera(newId(), name);
    }
}
