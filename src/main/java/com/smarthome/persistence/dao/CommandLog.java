package com.smarthome.persistence.dao;

import java.time.Instant;


// Immutable command log row returned from commands_log queries.
public record CommandLog(
    String commandId,
    String deviceId,
    String action,
    String paramsJson,
    String result,
    Instant timestamp
) {}
