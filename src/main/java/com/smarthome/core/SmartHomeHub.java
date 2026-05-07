package com.smarthome.core;

import com.smarthome.strategy.AutomationMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


// Singleton + Strategy context + room aggregate in one central hub.
public class SmartHomeHub implements RoomIterableCollection {
    // Eager singleton keeps construction simple and thread-safe.
    private static final SmartHomeHub INSTANCE = new SmartHomeHub();

    // roomId -> Room index for fast lookups.
    private final Map<String, Room> roomsById = new ConcurrentHashMap<>();
    // Current strategy to apply when automation mode is triggered.
    private AutomationMode automationMode;

    private SmartHomeHub() {
    }

    public static SmartHomeHub getInstance() {
        return INSTANCE;
    }

    public void addRoom(Room room) {
        Objects.requireNonNull(room, "room must not be null");
        roomsById.put(room.getRoomId(), room);
    }

    public Room getRoom(String roomId) {
        return roomsById.get(roomId);
    }

    public Collection<Room> getRooms() {
        // Return read-only view so callers cannot mutate hub internals by accident.
        return Collections.unmodifiableCollection(roomsById.values());
    }

    // ITERATOR pattern method (rubric requirement: returns Enumeration).
    public Enumeration<Room> enumerateRooms() {
        return Collections.enumeration(new ArrayList<>(roomsById.values()));
    }

    public void setAutomationMode(AutomationMode mode) {
        this.automationMode = mode;
    }

    public AutomationMode getAutomationMode() {
        return automationMode;
    }

    public void applyAutomationMode() {
        if (automationMode != null) {
            // Strategy pattern delegation point.
            automationMode.apply(this);
        }
    }

    @Override
    public RoomIterator createIterator() {
        return new HubRoomIterator(this);
    }
}
