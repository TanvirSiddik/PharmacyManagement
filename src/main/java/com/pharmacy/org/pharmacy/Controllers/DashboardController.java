package com.pharmacy.org.pharmacy.Controllers;

import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static DataBase.DatabaseConnection.getAuthName;

public class DashboardController {
    double xOffset;
    double yOffset;


    public Button inventory;
    public Button pageTwo;
    public VBox mainVbox;
    public Label authName;
    public MFXFontIcon closeIcon;
    public MFXFontIcon minimizeIcon;
    public MFXFontIcon alwaysOnTopIcon;
    public BorderPane borderPane;
    public HBox windowHeader;

    public void initialize() {
        callInventory();
        setUserName();
        customButtonAction();
        setupWindowDragging();
    }

    public void setupWindowDragging() {

        // Get the current stage dynamically if not already initialized
        windowHeader.setOnMousePressed(event -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            if (stage == null) {
                stage = (Stage) borderPane.getScene().getWindow();
            }
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        windowHeader.setOnMouseDragged(event -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            if (stage != null) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
    }

    public void customButtonAction() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) borderPane.getScene().getWindow()).setIconified(true));
    }

    public void setUserName() {
        String name = getAuthName != null && !getAuthName.isEmpty()
                ? getAuthName.substring(0, 1).toUpperCase() + getAuthName.substring(1).toLowerCase()
                : "Guest";
        authName.setText(name);
    }

    public void callInventory() {
        loadViewWithLoading("/com/pharmacy/org/pharmacy/inventory-view.fxml");
    }

    public void callPageTwo() {
        loadViewWithLoading("/com/pharmacy/org/pharmacy/add_medicine_view.fxml");
    }

    public void callPurchase(ActionEvent actionEvent) {
        loadViewWithLoading("/com/pharmacy/org/pharmacy/purchase-view.fxml");
    }

    public void callSummary(ActionEvent actionEvent) {
        loadViewWithLoading("/com/pharmacy/org/pharmacy/summary-view.fxml");
    }




    private void loadViewWithLoading(String fxmlFile) {
        // Create a ProgressIndicator
        MFXProgressSpinner loadingIndicator = new MFXProgressSpinner();
        mainVbox.setAlignment(Pos.CENTER);
        mainVbox.getChildren().clear(); // Clear the VBox
        mainVbox.getChildren().add(loadingIndicator); // Show the loading indicator

        // Create a Task to load the FXML in the background
        Task<VBox> loadViewTask = new Task<>() {
            @Override
            protected VBox call() throws IOException {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
                return loader.load();
            }
        };

        // On successful load, replace the loading indicator with the loaded content
        loadViewTask.setOnSucceeded(event -> {
            VBox loadedView = loadViewTask.getValue();
            mainVbox.getChildren().clear();
            mainVbox.getChildren().add(loadedView);
        });

        // On failure, log the error and optionally show an error message
        loadViewTask.setOnFailed(event -> {
            mainVbox.getChildren().clear();
            System.out.println("Error loading view: " + fxmlFile);
            loadViewTask.getException().printStackTrace();
            // Optionally, show an error message
            Label errorLabel = new Label("Failed to load the view. Please try again.");
            mainVbox.getChildren().add(errorLabel);
        });

        // Start the Task in a new thread
        Thread loadThread = new Thread(loadViewTask);
        loadThread.setDaemon(true); // Ensure the thread does not block JVM exit
        loadThread.start();
    }

}
