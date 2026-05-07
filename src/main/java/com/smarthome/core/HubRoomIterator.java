package com.smarthome.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;



public class HubRoomIterator implements RoomIterator {

    private final Enumeration<Room> rooms;

    public HubRoomIterator(SmartHomeHub hub) {
        this.rooms = Collections.enumeration(new ArrayList<>(hub.getRooms()));
    }

    @Override
    public Room getNext() {
        return rooms.nextElement();
    }

    @Override
    public boolean hasMore() {
        return rooms.hasMoreElements();
    }
}
