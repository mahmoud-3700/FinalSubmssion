package com.smarthome.facade;


// Immutable request payload for creating a scheduled action.
public record ScheduleRequest(String deviceId, String modeName, String cronExpression) {}
