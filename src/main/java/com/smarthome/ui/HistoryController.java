package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.observer.Observer;
import com.smarthome.persistence.dao.DeviceEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


// History screen controller that renders event and command timeline rows.
public class HistoryController implements Initializable {

    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_ROWS = 200;

    @FXML private VBox eventsContainer;

    private final com.smarthome.facade.HomeController facade =
        MainController.getFacade();

    private Observer liveObserver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHistoricalEvents();
        attachLiveObserver();
    }

    private void loadHistoricalEvents() {
        List<DeviceEvent> recent = facade.getEventHistory();
        for (DeviceEvent e : recent) {
            eventsContainer.getChildren().add(buildRowForPersisted(e));
        }
        if (recent.isEmpty()) {
            Label empty = new Label("No events yet — toggle some devices on the Home tab to see them appear here.");
            empty.getStyleClass().add("empty-state");
            empty.setWrapText(true);
            eventsContainer.getChildren().add(empty);
        }
    }

    private void attachLiveObserver() {
        liveObserver = (device, event) -> Platform.runLater(() ->
            prependRow(buildRowForLive(device.getName(), event)));
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            for (Device device : room.devices()) {
                device.attach(liveObserver);
            }
        }
    }

    private void prependRow(HBox row) {
        int insertAt = Math.min(2, eventsContainer.getChildren().size());
        eventsContainer.getChildren().add(insertAt, row);

        while (eventsContainer.getChildren().size() > MAX_ROWS + 2) {
            eventsContainer.getChildren().remove(eventsContainer.getChildren().size() - 1);
        }
    }

    private HBox buildRowForPersisted(DeviceEvent e) {
        String time = e.timestamp() != null
            ? e.timestamp().atZone(ZoneId.systemDefault()).toLocalTime().format(CLOCK)
            : "--:--:--";
        return buildRow(time, resolveDeviceName(e.deviceId()), e.eventType());
    }

    /**
     * Persisted DeviceEvents store only deviceId. Resolve to the live
     * device's display name when the device still exists in the hub;
     * fall back to the shortened id for events whose device has since
     * been removed (so the history row is still readable).
     */
    private String resolveDeviceName(String deviceId) {
        if (deviceId == null) return "Unknown device";
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            Device d = room.getDevice(deviceId);
            if (d != null) return d.getName();
        }
        return "device " + shortId(deviceId);
    }

    private HBox buildRowForLive(String deviceName, String eventType) {
        return buildRow(LocalTime.now().format(CLOCK), deviceName, eventType);
    }

    private HBox buildRow(String time, String deviceLabel, String eventType) {
        HBox row = new HBox(12);
        row.getStyleClass().add("event-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        StackPane avatar = new StackPane();
        avatar.getStyleClass().addAll("event-avatar", avatarClassFor(eventType));
        Label dot = new Label(dotFor(eventType));
        dot.getStyleClass().add(dotClassFor(eventType));
        avatar.getChildren().add(dot);

        VBox textBlock = new VBox(2);
        Label deviceLbl = new Label(deviceLabel);
        deviceLbl.getStyleClass().add("event-device");
        Label typeLabel = new Label(humanLabelFor(eventType));
        typeLabel.getStyleClass().add("event-type");
        textBlock.getChildren().addAll(deviceLbl, typeLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("event-time");
        timeLabel.setMinWidth(Region.USE_PREF_SIZE);

        row.getChildren().addAll(avatar, textBlock, spacer, timeLabel);
        return row;
    }

    private String avatarClassFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "event-avatar-on";
            case "LOCKED", "UNLOCKED" -> "event-avatar-locked";
            case "TEMP_CHANGED", "BRIGHTNESS_CHANGED" -> "event-avatar-temp";
            default -> "event-avatar-off";
        };
    }

    private String humanLabelFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "Turned on";
            case "TURNED_OFF" -> "Turned off";
            case "LOCKED" -> "Locked";
            case "UNLOCKED" -> "Unlocked";
            case "TEMP_CHANGED" -> "Temperature changed";
            case "BRIGHTNESS_CHANGED" -> "Brightness changed";
            default -> eventType.toLowerCase().replace('_', ' ');
        };
    }

    private String dotFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "●";
            case "TURNED_OFF" -> "○";
            case "LOCKED", "UNLOCKED" -> "▣";
            case "TEMP_CHANGED" -> "🌡";
            case "BRIGHTNESS_CHANGED" -> "✦";
            default -> "•";
        };
    }

    private String dotClassFor(String eventType) {
        return switch (eventType) {
            case "TURNED_ON" -> "event-dot-on";
            case "LOCKED", "UNLOCKED" -> "event-dot-locked";
            case "TEMP_CHANGED", "BRIGHTNESS_CHANGED" -> "event-dot-temp";
            default -> "event-dot-off";
        };
    }

    private String shortId(String id) {
        if (id == null) return "?";
        return id.length() > 8 ? id.substring(0, 8) + "…" : id;
    }
}
