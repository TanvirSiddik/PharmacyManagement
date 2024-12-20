package com.pharmacy.org.pharmacy.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static DataBase.DatabaseConnection.getAuthName;

public class DashboardController {

    public Button inventory;
    public Button pageTwo;
    public VBox mainVbox;
    public Label authName;

    public void initialize() {
        String name = getAuthName.substring(0, 1).toUpperCase() + getAuthName.substring(1).toLowerCase();
        authName.setText(name);
        callInventory();

    }

    public void callInventory() {
        loadView("/com/pharmacy/org/pharmacy/inventory-view.fxml");
    }

    public void callPageTwo() {
        loadView("/com/pharmacy/org/pharmacy/add_medicine_view.fxml");
    }

    public void callPurchase(ActionEvent actionEvent) {
        loadView("/com/pharmacy/org/pharmacy/purchase-view.fxml");
    }

    public void callSummary(ActionEvent actionEvent) {
        loadView("/com/pharmacy/org/pharmacy/summary-view.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile);  // Log the file being loaded
            mainVbox.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            mainVbox.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();  // Print the full stack trace for better debugging
            System.out.println("Error loading view: " + fxmlFile);
        }
    }


}
