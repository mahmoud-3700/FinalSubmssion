package com.smarthome.ui;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// UI event bus that triggers screen refresh callbacks on the JavaFX thread.
public final class HomeBus {

    // Thread-safe listener list because screens can subscribe/unsubscribe anytime.
    private static final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    private HomeBus() {}

    
    public static void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    
    public static void unsubscribe(Runnable listener) {
        listeners.remove(listener);
    }

    // Runs every listener callback on the UI thread.
    public static void notifyDataChanged() {
        Platform.runLater(() -> {
            for (Runnable r : new ArrayList<>(listeners)) {
                try {
                    r.run();
                } catch (Exception e) {
                    System.err.println("HomeBus listener threw: " + e.getMessage());
                }
            }
        });
    }
}
