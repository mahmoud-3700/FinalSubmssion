package com.smarthome.persistence.dao;

import java.time.Instant;


// Immutable device event row returned from device_events queries.
public record DeviceEvent(long eventId, String deviceId, String eventType, Instant timestamp) {}
