package com.smarthome.devices.version1;

import com.smarthome.devices.Lock;


// Version1 variant of Lock.
public class Version1Lock extends Lock {
    public Version1Lock(String id, String name) {
        super(id, name);
        lock();
    }
}
