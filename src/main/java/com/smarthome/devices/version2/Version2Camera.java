package com.smarthome.devices.version2;

import com.smarthome.devices.Camera;


// Version2 variant of Camera.
public class Version2Camera extends Camera {
    public Version2Camera(String id, String name) {
        super(id, name);
        turnOn();
    }
}
