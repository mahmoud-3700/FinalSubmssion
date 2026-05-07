package com.smarthome.ui;

import com.smarthome.core.Room;
import com.smarthome.core.SmartHomeHub;
import com.smarthome.devices.Device;
import com.smarthome.factory.Version2DeviceFactory;
import com.smarthome.persistence.Database;
import com.smarthome.persistence.dao.DeviceDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;
import com.smarthome.persistence.dao.RoomDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;


// JavaFX app bootstrapper that wires persistence, facade, and initial UI state.
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Database.getInstance();

        loadPersistedState();
        seedDemoDataIfEmpty();
        attachPersistenceObservers();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setTitle("Smart Home");
        stage.setScene(scene);
        stage.setMinWidth(360);
        stage.setMinHeight(640);
        stage.show();
    }

    
    private void loadPersistedState() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        RoomDAO roomDAO = new RoomDAO();
        DeviceDAO deviceDAO = new DeviceDAO();

        List<Room> rooms = roomDAO.findAll();
        for (Room r : rooms) {
            for (Device d : deviceDAO.findByRoom(r.getRoomId())) {
                r.addDevice(d);
            }
            hub.addRoom(r);
        }
    }

    
    private void seedDemoDataIfEmpty() {
        SmartHomeHub hub = SmartHomeHub.getInstance();
        if (!hub.getRooms().isEmpty()) {
            return;
        }
        Version2DeviceFactory factory = new Version2DeviceFactory();
        RoomDAO roomDAO = new RoomDAO();
        DeviceDAO deviceDAO = new DeviceDAO();

        Room kitchen = new Room("kitchen", "Kitchen");
        kitchen.addDevice(factory.createLight("Ceiling Light"));
        kitchen.addDevice(factory.createThermostat("Kitchen Thermostat"));
        hub.addRoom(kitchen);
        roomDAO.insert(kitchen);

        Room livingRoom = new Room("living-room", "Living Room");
        livingRoom.addDevice(factory.createLight("Lamp"));
        livingRoom.addDevice(factory.createCamera("Living Room Camera"));
        hub.addRoom(livingRoom);
        roomDAO.insert(livingRoom);

        Room frontDoor = new Room("front-door", "Front Door");
        frontDoor.addDevice(factory.createDoorLock("Smart Lock"));
        frontDoor.addDevice(factory.createCamera("Doorbell Camera"));
        hub.addRoom(frontDoor);
        roomDAO.insert(frontDoor);

        for (Room room : hub.getRooms()) {
            for (Device device : room.devices()) {
                deviceDAO.insert(device, room.getRoomId());
            }
        }
    }

    
    private void attachPersistenceObservers() {
        DaoEventBridge bridge = new DaoEventBridge(new DeviceEventDAO(), new DeviceDAO());
        for (Room room : SmartHomeHub.getInstance().getRooms()) {
            for (Device device : room.devices()) {
                device.attach(bridge);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
