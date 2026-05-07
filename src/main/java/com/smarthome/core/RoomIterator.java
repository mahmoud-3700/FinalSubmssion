package com.smarthome.core;



public interface RoomIterator {
    // Returns the next room and advances the cursor.
    Room getNext();

    // True while getNext() can still return another room.
    boolean hasMore();
}
