package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Camera;
import com.smarthome.devices.Device;
import com.smarthome.devices.Light;
import com.smarthome.devices.Lock;
import com.smarthome.devices.Thermostat;
import com.smarthome.observer.Observer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


// Home screen controller that renders room and device cards and actions.
public class HomeController implements Initializable {

    @FXML private VBox roomsContainer;

    private final com.smarthome.facade.HomeController facade =
        MainController.getFacade();

    private Observer cardRefreshObserver;

    private final Runnable busListener = this::renderAllRooms;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cardRefreshObserver = (device, event) ->
            Platform.runLater(this::renderAllRooms);
        attachToAllDevices(cardRefreshObserver);

        HomeBus.subscribe(busListener);

        renderAllRooms();
    }

    private void renderAllRooms() {
        roomsContainer.getChildren().clear();
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            roomsContainer.getChildren().add(buildRoomSection(room));
        }
    }

    private VBox buildRoomSection(Room room) {
        VBox section = new VBox(8);

        HBox header = new HBox(8);
        header.getStyleClass().add("room-header");
        Label name = new Label(room.getName());
        name.getStyleClass().add("room-name");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label count = new Label(deviceCount(room) + " devices");
        count.getStyleClass().add("room-count");
        header.getChildren().addAll(name, spacer, count);

        section.getChildren().add(header);

        for (Device device : room.devices()) {
            section.getChildren().add(buildDeviceCard(device));
        }

        Button addDevice = new Button("＋  Add device to " + room.getName());
        addDevice.getStyleClass().addAll("action-button", "action-button-outline");
        addDevice.setMaxWidth(Double.MAX_VALUE);
        addDevice.setOnAction(e -> openAddDeviceModal(room));
        section.getChildren().add(addDevice);

        return section;
    }

    
    private void openAddDeviceModal(Room room) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add-device.fxml"));
            Parent modalRoot = loader.load();
            AddDeviceController controller = loader.getController();
            controller.setTargetRoom(room);

            Stage modal = new Stage();
            modal.setTitle("Add device");
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(roomsContainer.getScene().getWindow());

            Scene scene = new Scene(modalRoot);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            modal.setScene(scene);
            modal.showAndWait();
        } catch (Exception ex) {
            System.err.println("Could not open Add Device modal: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private VBox buildDeviceCard(Device device) {
        VBox card = new VBox(12);
        card.getStyleClass().add("device-card");

        HBox top = new HBox(14);
        top.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        StackPane avatar = buildAvatar(device);

        VBox nameBlock = new VBox(2);
        Label nameLabel = new Label(device.getName());
        nameLabel.getStyleClass().add("device-name");
        Label subtitle = new Label(subtitleFor(device));
        subtitle.getStyleClass().add("device-subtitle");
        nameBlock.getChildren().addAll(nameLabel, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label chip = stateChipFor(device);

        top.getChildren().addAll(avatar, nameBlock, spacer, chip);

        card.getChildren().add(top);

        HBox actions = buildActionsFor(device);
        card.getChildren().add(actions);

        return card;
    }

    /**
     * Builds an M3 leading-icon avatar — a circular tonal container around
     * the device's emoji icon. The container's tone shifts based on state
     * (primary-container when on, secondary-container when locked, neutral
     * surface-high otherwise) so the card communicates state at a glance
     * even before the user reads the chip.
     */
    private StackPane buildAvatar(Device device) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("device-avatar");

        Label icon = new Label(iconFor(device));
        icon.getStyleClass().add("device-icon");

        if (device instanceof Lock lock) {
            if (lock.isLocked()) {
                avatar.getStyleClass().add("device-avatar-locked");
                icon.getStyleClass().add("device-icon-locked");
            }
        } else if (device instanceof Thermostat) {
            avatar.getStyleClass().add("device-avatar-on");
            icon.getStyleClass().add("device-icon-on");
        } else if (device.isPoweredOn()) {
            avatar.getStyleClass().add("device-avatar-on");
            icon.getStyleClass().add("device-icon-on");
        }

        avatar.getChildren().add(icon);
        return avatar;
    }

    private HBox buildActionsFor(Device device) {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        if (device instanceof Lock lock) {
            row.getChildren().add(lock.isLocked()
                ? actionButton("Unlock", () -> facade.unlockDevice(device.getId()))
                : actionButton("Lock",   () -> facade.lockDevice(device.getId())));
            return row;
        }

        if (device instanceof Thermostat thermostat) {
            Button minus = spinnerButton("−", () ->
                facade.setTemperature(device.getId(), thermostat.getTemperature() - 1));
            Button plus  = spinnerButton("+", () ->
                facade.setTemperature(device.getId(), thermostat.getTemperature() + 1));
            Label tempDisplay = new Label(String.format("%.0f°C", thermostat.getTemperature()));
            tempDisplay.getStyleClass().add("temperature-display");
            row.getChildren().addAll(minus, tempDisplay, plus);
            return row;
        }

        if (device.isPoweredOn()) {
            row.getChildren().add(
                actionButton("Turn Off", () -> facade.turnOffDevice(device.getId())));
        } else {
            Button on = actionButton("Turn On", () -> facade.turnOnDevice(device.getId()));
            on.getStyleClass().add("action-button-primary");
            row.getChildren().add(on);
        }
        return row;
    }

    private Button actionButton(String text, Runnable handler) {
        Button b = new Button(text);
        b.getStyleClass().add("action-button");
        b.setOnAction(e -> safeRun(handler));
        return b;
    }

    private Button spinnerButton(String text, Runnable handler) {
        Button b = new Button(text);
        b.getStyleClass().add("spinner-button");
        b.setOnAction(e -> safeRun(handler));
        return b;
    }

    private void safeRun(Runnable handler) {
        try {
            handler.run();
        } catch (Exception e) {
            System.err.println("Action failed: " + e.getMessage());
        }
    }

    private String iconFor(Device d) {
        if (d instanceof Light)      return "💡";
        if (d instanceof Thermostat) return "🌡";
        if (d instanceof Lock)       return "🔒";
        if (d instanceof Camera)     return "📷";
        return "•";
    }

    private String subtitleFor(Device d) {
        String type;
        if (d instanceof Light)           type = "Lighting";
        else if (d instanceof Thermostat) type = "Climate";
        else if (d instanceof Lock)       type = "Security";
        else if (d instanceof Camera)     type = "Security";
        else                              type = "Device";

        String pkg = d.getClass().getPackageName();
        String family = pkg.contains("version2") ? "Version2"
                      : pkg.contains("version1") ? "Version1"
                      : "Base";
        return type + " · " + family;
    }

    /**
     * M3 assist chip showing the device's current state. Pill-shaped,
     * tonal background that follows the same color logic as the avatar
     * so the two reinforce each other.
     */
    private Label stateChipFor(Device d) {
        Label chip = new Label();
        chip.getStyleClass().add("state-chip");
        if (d instanceof Lock lock) {
            chip.setText(lock.isLocked() ? "LOCKED" : "UNLOCKED");
            chip.getStyleClass().add(lock.isLocked()
                ? "state-chip-locked" : "state-chip-off");
        } else if (d instanceof Thermostat thermostat) {
            chip.setText(String.format("%.0f°C", thermostat.getTemperature()));
            chip.getStyleClass().add("state-chip-on");
        } else {
            chip.setText(d.isPoweredOn() ? "ON" : "OFF");
            chip.getStyleClass().add(d.isPoweredOn()
                ? "state-chip-on" : "state-chip-off");
        }
        return chip;
    }

    private int deviceCount(Room room) {
        return room.devices().size();
    }

    private void attachToAllDevices(Observer obs) {
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            for (Device device : room.devices()) {
                device.attach(obs);
            }
        }
    }
}
