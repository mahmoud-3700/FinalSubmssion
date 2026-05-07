package com.smarthome.ui;

import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;

import java.util.Objects;


// Observer that persists device events and latest device state to the database.
public class DaoEventBridge implements Observer {

    // Writes append-only audit events.
    private final DeviceEventDAO eventDao;
    // Writes current device snapshot (optional for audit-only usage).
    private final DeviceDAO deviceDao;

    // Convenience constructor used where only event history is required.
    public DaoEventBridge(DeviceEventDAO eventDao) {
        this(eventDao, null);
    }

    // Full constructor used by app startup to keep history and live state in sync.
    public DaoEventBridge(DeviceEventDAO eventDao, DeviceDAO deviceDao) {
        this.eventDao = Objects.requireNonNull(eventDao, "eventDao must not be null");
        this.deviceDao = deviceDao;
    }

    @Override
    public void update(Device d, String event) {
        // Observer pattern bridge: domain event -> persistent audit row.
        try {
            eventDao.insert(d.getId(), event);
        } catch (Exception e) {
            System.err.println("DaoEventBridge: failed to persist event " + event
                + " for device " + d.getId() + ": " + e.getMessage());
        }
        if (deviceDao != null) {
            // Keep devices table aligned with latest in-memory state.
            try {
                deviceDao.updateState(d);
            } catch (Exception e) {
                System.err.println("DaoEventBridge: failed to update device row for "
                    + d.getId() + ": " + e.getMessage());
            }
        }
    }
}
