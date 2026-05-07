package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Thermostat;

import java.util.Objects;


// Concrete Strategy: reduce energy use without fully shutting the house down.
public class EcoMode implements AutomationMode {
    @Override
    public String name() {
        return "ECO";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");
        // Strategy owns these decisions; SmartHomeHub only delegates.
        for (Room room : hub.getRooms()) {
            for (Device device : room.devices()) {
                if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(24.0);
                }
                if (device instanceof Light light && light.isPoweredOn()) {
                    light.setBrightness(50);
                }
            }
        }
    }
}
