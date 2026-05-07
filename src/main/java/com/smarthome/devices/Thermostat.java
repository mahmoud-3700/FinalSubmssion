package com.smarthome.devices;


// Thermostat device storing target temperature in Celsius.
public class Thermostat extends Device {
    private double temperatureC = 22.0;

    public Thermostat(String id, String name) {
        super(id, name);
    }

    public double getTemperature() {
        return temperatureC;
    }

    public void setTemperature(double value) {
        this.temperatureC = value;
        notifyObservers(EVENT_TEMP_CHANGED);
    }
}
