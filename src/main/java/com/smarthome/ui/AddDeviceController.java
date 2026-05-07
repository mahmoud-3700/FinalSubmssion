package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.devices.Device;
import com.smarthome.factory.DeviceFactory;
import com.smarthome.factory.Version1DeviceFactory;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


// Modal controller for creating a device in a selected room.
public class AddDeviceController implements Initializable {

    @FXML private Label titleLabel;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private ChoiceBox<String> familyChoice;
    @FXML private TextField nameField;
    @FXML private Label errorLabel;

    
    private Room targetRoom;

    public void setTargetRoom(Room room) {
        this.targetRoom = room;
        if (titleLabel != null && room != null) {
            titleLabel.setText("Add device to " + room.getName());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeChoice.setItems(FXCollections.observableArrayList(
            "Light", "Thermostat", "Lock", "Camera"));
        typeChoice.getSelectionModel().select("Light");

        familyChoice.setItems(FXCollections.observableArrayList(
            "Version2 (modern)", "Version1 (legacy)"));
        familyChoice.getSelectionModel().select("Version2 (modern)");
    }

    @FXML
    private void onAdd() {
        if (targetRoom == null) {
            showError("No target room set.");
            return;
        }
        String type = typeChoice.getValue();
        String family = familyChoice.getValue();
        String name = nameField.getText() == null ? "" : nameField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter a name.");
            return;
        }

        try {
            DeviceFactory factory = family != null && family.startsWith("Version1")
                ? new Version1DeviceFactory()
                : new Version2DeviceFactory();

            Device device = switch (type) {
                case "Light"      -> factory.createLight(name);
                case "Thermostat" -> factory.createThermostat(name);
                case "Lock"       -> factory.createDoorLock(name);
                case "Camera"     -> factory.createCamera(name);
                default -> throw new IllegalArgumentException("Pick a device type");
            };

            targetRoom.addDevice(device);

            new DeviceDAO().insert(device, targetRoom.getRoomId());

            device.attach(new DaoEventBridge(new DeviceEventDAO(), new DeviceDAO()));

            HomeBus.notifyDataChanged();

            close();
        } catch (Exception e) {
            showError("Failed to add: " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: -danger;");
    }

    private void close() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
