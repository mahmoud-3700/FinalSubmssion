package com.smarthome.devices.version1;

import com.smarthome.devices.Light;


// Version1 variant of Light.
public class Version1Light extends Light {
    public Version1Light(String id, String name) {
        super(id, name);
    }

    @Override
    public void setBrightness(int value) {
        int clamped = Math.max(0, Math.min(100, value));
        int stepped = (clamped / 25) * 25;
        super.setBrightness(stepped);
    }
}
