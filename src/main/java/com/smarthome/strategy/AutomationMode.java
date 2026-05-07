package com.smarthome.strategy;

import com.smarthome.core.SmartHomeHub;


// Strategy contract for applying one automation mode to the hub.
public interface AutomationMode {
    // Stable identifier shown in UI and logs.
    String name();

    // Strategy entry point: receives the hub context and applies mode-specific rules.
    void apply(SmartHomeHub hub);
}
