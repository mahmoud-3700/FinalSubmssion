package com.smarthome.devices.version1;

import com.smarthome.devices.Thermostat;


// Version1 variant of Thermostat.
public class Version1Thermostat extends Thermostat {
    public Version1Thermostat(String id, String name) {
        super(id, name);
        setTemperature(20.0);
    }

    @Override
    public void setTemperature(double value) {
        double clamped = Math.max(10.0, Math.min(30.0, value));
        double rounded = Math.rint(clamped);
        super.setTemperature(rounded);
    }
}
