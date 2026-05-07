package com.smarthome.ui;

import com.smarthome.command.CommandInvoker;
import com.smarthome.facade.HomeController;
import com.smarthome.persistence.dao.CommandsLogDAO;
import com.smarthome.persistence.dao.DeviceEventDAO;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Optional;

import java.net.URL;
import java.util.ResourceBundle;


// Root UI controller for top bar, navigation, and shared facade wiring.
public class MainController implements Initializable {

    @FXML private Button undoButton;
    @FXML private Button modeEcoButton;
    @FXML private Button modeSleepButton;
    @FXML private Button modeAwayButton;
    @FXML private HBox statusBanner;
    @FXML private Label statusBannerText;
    @FXML private StackPane screenHost;
    @FXML private Button navHomeButton;
    @FXML private Button navHistoryButton;

    
    private static HomeController sharedFacade;

    public static HomeController getFacade() {
        if (sharedFacade == null) {
            sharedFacade = buildProductionFacade();
        }
        return sharedFacade;
    }

    private static HomeController buildProductionFacade() {
        DeviceEventDAO eventDAO = new DeviceEventDAO();
        CommandsLogDAO commandsDAO = new CommandsLogDAO();
        CommandInvoker invoker = new CommandInvoker(commandsDAO);
        return new HomeController(
            com.smarthome.core.SmartHomeHub.getInstance(),
            invoker,
            eventDAO,
            commandsDAO
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sharedFacade = buildProductionFacade();

        loadScreen("/fxml/home.fxml", navHomeButton);
    }

    @FXML
    private void onUndo() {
        boolean undone = sharedFacade.undoLastAction();
        if (undone) {
            showBanner("✓ Undid last action.", "status-banner-success");
            HomeBus.notifyDataChanged();
        } else {
            showBanner("Nothing to undo.", null);
        }
    }

    @FXML private void onModeEco()   { applyMode("ECO",   modeEcoButton);   }
    @FXML private void onModeSleep() { applyMode("SLEEP", modeSleepButton); }
    @FXML private void onModeAway()  { applyMode("AWAY",  modeAwayButton);  }

    private void applyMode(String name, Button activated) {
        if (!confirmModeChange(name)) {
            return;
        }
        try {
            sharedFacade.setAutomationMode(name);
            highlightMode(activated);
            showBanner("✓ " + name + " mode applied to the whole home.",
                       "status-banner-success");
            HomeBus.notifyDataChanged();
        } catch (Exception e) {
            showBanner("Could not apply " + name + ": " + e.getMessage(),
                       "status-banner-error");
        }
    }

    
    private boolean confirmModeChange(String name) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm automation mode");
        alert.setHeaderText("Apply " + name + " mode?");
        alert.setContentText(consequenceFor(name) + "\n\nThis can be reversed with the Undo button.");
        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private String consequenceFor(String name) {
        return switch (name) {
            case "ECO"   -> "ECO will set every thermostat to 24°C and dim every powered-on light to 50%.";
            case "SLEEP" -> "SLEEP will turn off every light, lock every door, and set thermostats to 20°C.";
            case "AWAY"  -> "AWAY will turn off every light, lock every door, arm every camera, and set thermostats to 15°C.";
            default      -> "This will change the state of multiple devices across every room.";
        };
    }

    private void highlightMode(Button activated) {
        clearActive(modeEcoButton, modeSleepButton, modeAwayButton);
        addClass(activated, "mode-segment-active");
    }

    private void clearActive(Button... buttons) {
        for (Button b : buttons) {
            b.getStyleClass().removeAll("mode-segment-active");
        }
    }

    private void addClass(Node node, String cls) {
        if (!node.getStyleClass().contains(cls)) {
            node.getStyleClass().add(cls);
        }
    }

    public void showBanner(String text, String variantClass) {
        statusBannerText.setText(text);
        statusBanner.getStyleClass().removeAll(
            "status-banner-success", "status-banner-error");
        if (variantClass != null) {
            addClass(statusBanner, variantClass);
        }
        statusBanner.setManaged(true);
        statusBanner.setVisible(true);

        PauseTransition fade = new PauseTransition(Duration.seconds(4));
        fade.setOnFinished(e -> {
            statusBanner.setManaged(false);
            statusBanner.setVisible(false);
        });
        fade.play();
    }

    @FXML private void onNavHome()    { loadScreen("/fxml/home.fxml",    navHomeButton);    }
    @FXML private void onNavHistory() { loadScreen("/fxml/history.fxml", navHistoryButton); }

    private void loadScreen(String fxmlPath, Button activated) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node screen = loader.load();
            screenHost.getChildren().setAll(screen);

            highlightNav(activated);
        } catch (Exception e) {
            showBanner("Failed to load screen: " + e.getMessage(),
                       "status-banner-error");
            e.printStackTrace();
        }
    }

    private void highlightNav(Button activated) {
        for (Button b : new Button[]{navHomeButton, navHistoryButton}) {
            b.getStyleClass().removeAll("nav-button-active");
        }
        addClass(activated, "nav-button-active");
    }

}
