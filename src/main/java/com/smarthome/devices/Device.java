package com.smarthome.devices;

import com.smarthome.observer.Observable;
import com.smarthome.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Base observable device with id, name, and shared power-state behavior.
public abstract class Device implements Observable {
    public static final String EVENT_TURNED_ON = "TURNED_ON";
    public static final String EVENT_TURNED_OFF = "TURNED_OFF";
    public static final String EVENT_TEMP_CHANGED = "TEMP_CHANGED";
    public static final String EVENT_LOCKED = "LOCKED";
    public static final String EVENT_UNLOCKED = "UNLOCKED";

    private final List<Observer> observers = new ArrayList<>();
    private final String id;
    private String name;
    private boolean poweredOn;

    protected Device(String id, String name) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public boolean isPoweredOn() {
        return poweredOn;
    }

    public void turnOn() {
        if (!poweredOn) {
            poweredOn = true;
            notifyObservers(EVENT_TURNED_ON);
        }
    }

    public void turnOff() {
        if (poweredOn) {
            poweredOn = false;
            notifyObservers(EVENT_TURNED_OFF);
        }
    }

    @Override
    public void attach(Observer observer) {
        Objects.requireNonNull(observer, "observer must not be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        for (Observer observer : List.copyOf(observers)) {
            observer.update(this, event);
        }
    }
}
