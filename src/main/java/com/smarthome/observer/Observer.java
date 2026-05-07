package com.smarthome.observer;

import com.smarthome.devices.Device;


// Observer side of the Observer pattern.
public interface Observer {
    // Called by Observable subjects (for example Device) after state changes.
    void update(Device d, String event);
}
