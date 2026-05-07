package com.smarthome.strategy;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;

import java.util.Objects;


// Concrete Strategy: away profile for security and low energy usage.
public class AwayMode implements AutomationMode {
    @Override
    public String name() {
        return "AWAY";
    }

    @Override
    public void apply(SmartHomeHub hub) {
        Objects.requireNonNull(hub, "hub must not be null");
        for (Room room : hub.getRooms()) {
            for (Device device : room.devices()) {
                if (device instanceof Light light) {
                    light.turnOff();
                }
                if (device instanceof Lock lock) {
                    lock.lock();
                }
                if (device instanceof Camera camera) {
                    // Keeps camera power policy in one mode class, not spread across UI code.
                    camera.turnOn();
                }
                if (device instanceof Thermostat thermostat) {
                    thermostat.setTemperature(15.0);
                }
            }
        }
    }
}
