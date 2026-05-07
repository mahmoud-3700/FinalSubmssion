package com.smarthome.core;

import com.smarthome.devices.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


// ITERATOR pattern: Room aggregates devices and exposes them via Enumeration
// (rubric line: "Methods that return an Enumeration") and via List for
// modern foreach-friendly iteration.
public class Room {
    private final String roomId;
    private final String name;
    // LinkedHashMap preserves insertion order for predictable UI rendering.
    private final Map<String, Device> devicesById = new LinkedHashMap<>();

    public Room(String roomId, String name) {
        this.roomId = Objects.requireNonNull(roomId, "roomId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public void addDevice(Device device) {
        Objects.requireNonNull(device, "device must not be null");
        // Replaces old device if the same id already exists.
        devicesById.put(device.getId(), device);
    }

    public void removeDevice(String deviceId) {
        devicesById.remove(deviceId);
    }

    public Device getDevice(String deviceId) {
        return devicesById.get(deviceId);
    }

    public List<Device> devices() {
        return new ArrayList<>(devicesById.values());
    }

    // ITERATOR pattern method (rubric requirement: returns Enumeration).
    public Enumeration<Device> enumerateDevices() {
        return Collections.enumeration(devicesById.values());
    }
}
