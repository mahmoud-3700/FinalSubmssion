package com.smarthome.observer;


// Subject side of the Observer pattern.
public interface Observable {
    // Register an observer to receive future events.
    void attach(Observer observer);

    // Stop sending events to an observer.
    void detach(Observer observer);

    // Broadcast a named event to all current observers.
    void notifyObservers(String event);
}
