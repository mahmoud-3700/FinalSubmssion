package com.smarthome.facade;

import com.smarthome.command.CommandInvoker;
import com.smarthome.command.LockCommand;
import com.smarthome.command.SetAutomationModeCommand;
import com.smarthome.command.SetTemperatureCommand;
import com.smarthome.command.TurnOffCommand;
import com.smarthome.command.TurnOnCommand;
import com.smarthome.command.UnlockCommand;
import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.persistence.dao.CommandLog;
import com.smarthome.persistence.dao.CommandsLogDAO;
import com.smarthome.persistence.dao.DeviceEvent;
import com.smarthome.persistence.dao.DeviceEventDAO;
import com.smarthome.strategy.AutomationMode;
import com.smarthome.strategy.AwayMode;
import com.smarthome.strategy.EcoMode;
import com.smarthome.strategy.SleepMode;

import java.util.List;
import java.util.Locale;


// Facade pattern entry point for UI: one API over many subsystems.
public class HomeController {

    private final SmartHomeHub hub;
    private final CommandInvoker invoker;
    private final DeviceEventDAO eventDAO;
    private final CommandsLogDAO commandsLogDAO;

    // Default wiring for app runtime.
    public HomeController() {
        this(
            SmartHomeHub.getInstance(),
            new CommandInvoker(),
            null,    // event DAO is optional — UI can show in-memory state if DB not initialised
            null     // commands DAO is optional — same reason
        );
    }

    public HomeController(SmartHomeHub hub,
                          CommandInvoker invoker,
                          DeviceEventDAO eventDAO,
                          CommandsLogDAO commandsLogDAO) {
        this.hub = hub;
        this.invoker = invoker;
        this.eventDAO = eventDAO;
        this.commandsLogDAO = commandsLogDAO;
    }

    public void turnOnDevice(String deviceId) {
        // Facade delegates mutation to Command pattern instead of mutating directly.
        invoker.execute(new TurnOnCommand(findDevice(deviceId)));
    }

    public void turnOffDevice(String deviceId) {
        invoker.execute(new TurnOffCommand(findDevice(deviceId)));
    }

    public void lockDevice(String deviceId) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Lock lock)) {
            throw new IllegalArgumentException(deviceId + " is not a Lock");
        }
        invoker.execute(new LockCommand(lock));
    }

    public void unlockDevice(String deviceId) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Lock lock)) {
            throw new IllegalArgumentException(deviceId + " is not a Lock");
        }
        invoker.execute(new UnlockCommand(lock));
    }

    public void setTemperature(String deviceId, double value) {
        Device d = findDevice(deviceId);
        if (!(d instanceof Thermostat thermostat)) {
            throw new IllegalArgumentException(deviceId + " is not a Thermostat");
        }
        invoker.execute(new SetTemperatureCommand(thermostat, value));
    }

    public void setAutomationMode(String modeName) {
        // Converts simple UI input into a concrete Strategy instance.
        AutomationMode mode = resolveMode(modeName);
        invoker.execute(new SetAutomationModeCommand(hub, mode));
    }

    
    public boolean undoLastAction() {
        if (!invoker.canUndo()) {
            return false;
        }
        invoker.undo();
        return true;
    }

    public List<Device> getDevicesForRoom(String roomId) {
        Room room = hub.getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Unknown room: " + roomId);
        }
        return room.devices();
    }

    public List<DeviceEvent> getEventHistory() {
        if (eventDAO == null) return List.of();
        return eventDAO.findRecent(100);
    }

    public List<CommandLog> getCommandHistory() {
        if (commandsLogDAO == null) return List.of();
        return commandsLogDAO.findRecent(100);
    }

    // Stub intentionally left for the schedule module.
    public void createSchedule(ScheduleRequest request) {
        throw new UnsupportedOperationException(
            "Schedule executor not yet wired — see M4 task. Request: " + request);
    }

    // Local helper keeps device lookup logic out of UI controllers.
    private Device findDevice(String deviceId) {
        for (Room room : hub.getRooms()) {
            Device d = room.getDevice(deviceId);
            if (d != null) return d;
        }
        throw new IllegalArgumentException("Unknown device: " + deviceId);
    }

    // Strategy factory method by user-facing mode string.
    private AutomationMode resolveMode(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException("modeName must not be null");
        }
        return switch (modeName.toUpperCase(Locale.ROOT)) {
            case "ECO"   -> new EcoMode();
            case "SLEEP" -> new SleepMode();
            case "AWAY"  -> new AwayMode();
            default -> throw new IllegalArgumentException("Unknown mode: " + modeName);
        };
    }
}
