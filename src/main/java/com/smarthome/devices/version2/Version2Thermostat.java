package com.smarthome.devices.version2;

import com.smarthome.devices.Thermostat;


// Version2 variant of Thermostat.
public class Version2Thermostat extends Thermostat {
    public Version2Thermostat(String id, String name) {
        super(id, name);
    }

    @Override
    public void setTemperature(double value) {
        double clamped = Math.max(5.0, Math.min(35.0, value));
        super.setTemperature(clamped);
    }
}
