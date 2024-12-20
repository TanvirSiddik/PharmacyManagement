package com.pharmacy.org.pharmacy.Controllers;

import DataBase.DatabaseConnection;
import com.pharmacy.org.pharmacy.Models.Medicine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddMedicineController {
    public Button addButton;
    @FXML
    private TableColumn<Medicine, String> names;

    @FXML
    public TableColumn<Medicine, String> generic;

    @FXML
    public TableColumn<Medicine, String> strength;

    @FXML
    public TableColumn<Medicine, String> manufacturer;

    @FXML
    public TableColumn<Medicine, String> prices;

    @FXML
    private TableView<Medicine> inventory;

    @FXML
    private TextField searchField;

    private final ObservableList<Medicine> medName = FXCollections.observableArrayList();

    public void initialize() {
        try {
            loadMedicinesFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set up column binding
        names.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        generic.setCellValueFactory(new PropertyValueFactory<>("genericName"));
        strength.setCellValueFactory(new PropertyValueFactory<>("strength"));
        manufacturer.setCellValueFactory(new PropertyValueFactory<>("manufacture"));
        prices.setCellValueFactory(new PropertyValueFactory<>("packageContainer"));

        // Create a FilteredList to support searching
        FilteredList<Medicine> filteredData = new FilteredList<>(medName, _ -> true);

        // Add a listener to the search field
        searchField.textProperty().addListener((_, _, newValue) ->
                filteredData.setPredicate(medicine -> {
                    // If the search field is empty, show all items
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    // Convert search query to lowercase for case-insensitive matching
                    String lowerCaseFilter = newValue.toLowerCase();

                    // Compare search query with each column's data
                    return medicine.getBrandName().toLowerCase().contains(lowerCaseFilter) ||
                            medicine.getGenericName().toLowerCase().contains(lowerCaseFilter) ||
                            medicine.getStrength().toLowerCase().contains(lowerCaseFilter) ||
                            medicine.getManufacture().toLowerCase().contains(lowerCaseFilter);
                })
        );

        // Bind the filtered data to the TableView
        inventory.setItems(filteredData);
    }

    private void loadMedicinesFromDatabase() throws SQLException {
        Connection data = DatabaseConnection.con();
        String query = "SELECT * FROM Medicines";

        try (PreparedStatement state = data.prepareStatement(query);
             ResultSet resultSet = state.executeQuery()) {

            while (resultSet.next()) {
                String brandName = resultSet.getString("brand_name");
                String genericName = resultSet.getString("generic");
                String strength = resultSet.getString("strength");
                String manufacture = resultSet.getString("manufacturer");
                String packageContainer = resultSet.getString("package container");

                medName.add(new Medicine(brandName, genericName, strength, manufacture, packageContainer));
            }
        }
    }

    @FXML
    private void addSelectedMedicineToInventory() {
        Medicine selectedMedicine = inventory.getSelectionModel().getSelectedItem();

        if (selectedMedicine != null) {
            try (Connection data = DatabaseConnection.con()) {
                // Check if the medicine already exists in the inventory
                String checkQuery = "SELECT quantity FROM inventory WHERE brand_name = ? AND generic_name = ? AND strength = ? AND manufacturer = ? AND price = ?";
                try (PreparedStatement checkStatement = data.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, selectedMedicine.getBrandName());
                    checkStatement.setString(2, selectedMedicine.getGenericName());
                    checkStatement.setString(3, selectedMedicine.getStrength());
                    checkStatement.setString(4, selectedMedicine.getManufacture());
                    checkStatement.setString(5, selectedMedicine.getPackageContainer());

                    ResultSet resultSet = checkStatement.executeQuery();
                    if (resultSet.next()) {
                        // Medicine exists in inventory
                        int existingQuantity = resultSet.getInt("quantity");
                        double existingPrice = resultSet.getDouble("price");

                        // Custom dialog for quantity and price
                        Dialog<Pair<Integer, Double>> dialog = createCustomDialog(
                                "Update Inventory",
                                "Medicine exists in inventory.\nCurrent Quantity: " + existingQuantity + ", Price: ৳ " + existingPrice,
                                existingQuantity,
                                existingPrice
                        );

                        Optional<Pair<Integer, Double>> result = dialog.showAndWait();
                        result.ifPresent(pair -> {
                            int newQuantity = pair.getKey();
                            double newPrice = pair.getValue();

                            try {
                                String updateQuery = "UPDATE inventory SET quantity = ?, price = ? WHERE brand_name = ? AND generic_name = ? AND strength = ? AND manufacturer = ? AND sell_price = ?";
                                try (PreparedStatement updateStatement = data.prepareStatement(updateQuery)) {
                                    updateStatement.setInt(1, newQuantity);
                                    updateStatement.setString(2, selectedMedicine.getPackageContainer());
                                    updateStatement.setString(3, selectedMedicine.getBrandName());
                                    updateStatement.setString(4, selectedMedicine.getGenericName());
                                    updateStatement.setString(5, selectedMedicine.getStrength());
                                    updateStatement.setString(6, selectedMedicine.getManufacture());
                                    updateStatement.setDouble(7, newPrice);

                                    int rowsUpdated = updateStatement.executeUpdate();
                                    if (rowsUpdated > 0) {
                                        showUpdateSuccessAlert(selectedMedicine, newQuantity);
                                    }
                                }
                            } catch (SQLException e) {
                                showErrorAlert();
                            }
                        });
                        return; // Exit after handling existing item
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert();
                return;
            }

            // For new medicine
            Dialog<Pair<Integer, Double>> dialog = createCustomDialog(
                    "Add to Inventory",
                    "Adding new medicine to inventory.",
                    100,
                    10.00
            );

            Optional<Pair<Integer, Double>> result = dialog.showAndWait();
            result.ifPresent(pair -> {
                int quantity = pair.getKey();
                double unitPrice = pair.getValue();

                try (Connection data = DatabaseConnection.con()) {
                    String insertQuery = "INSERT INTO inventory (brand_name, generic_name, strength, manufacturer, price, quantity, sell_price) VALUES (?, ?, ?, ?, ?, ?,?)";
                    try (PreparedStatement insertStatement = data.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, selectedMedicine.getBrandName());
                        insertStatement.setString(2, selectedMedicine.getGenericName());
                        insertStatement.setString(3, selectedMedicine.getStrength());
                        insertStatement.setString(4, selectedMedicine.getManufacture());
                        insertStatement.setDouble(5, unitPrice);
                        insertStatement.setInt(6, quantity);
                        insertStatement.setDouble(7, unitPrice);

                        int rowsInserted = insertStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            showSuccessAlert(selectedMedicine, quantity, unitPrice * quantity);
                        }
                    }
                } catch (SQLException e) {
                    showErrorAlert();
                }
            });
        } else {
            showNoSelectionAlert();
        }
    }

    private Dialog<Pair<Integer, Double>> createCustomDialog(String title, String header, int defaultQuantity, double defaultPrice) {
        Dialog<Pair<Integer, Double>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        // Set the button types
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField quantityField = new TextField(String.valueOf(defaultQuantity));
        TextField priceField = new TextField(String.format("%.2f", defaultPrice));

        grid.add(new Label("Quantity:"), 0, 0);
        grid.add(quantityField, 1, 0);
        grid.add(new Label("Price (৳):"), 0, 1);
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a pair when the OK button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    return new Pair<>(quantity, price);
                } catch (NumberFormatException e) {
                    showInvalidInputAlert("quantity and price");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    // Duplicate alert method
    private void showDuplicateAlert(Medicine medicine) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Duplicate Item");
        alert.setHeaderText("Medicine Already Exist");
        alert.setContentText("The medicine \"" + medicine.getBrandName() + "\" already exists in the inventory.");
        alert.showAndWait();
    }


    // Method to extract the price from the string after the '৳' symbol
    private double extractPriceFromString(String priceString) {
        String regex = "৳\\s*(\\d+\\.\\d{2})"; // Regex to match the price after '৳'

        // Use regex to extract the price
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(priceString);

        if (matcher.find()) {
            // Convert the extracted string to double
            return Double.parseDouble(matcher.group(1));
        }

        // Return 0.0 if no price is found
        return 0.0;
    }

    // Success alert that shows the total price
    private void showSuccessAlert(Medicine medicine, int quantity, double totalPrice) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Item Added");
        alert.setHeaderText("Success");
        alert.setContentText("The medicine \"" + medicine.getBrandName() + "\" has been successfully added to the inventory with quantity: "
                + quantity + ". Total Price: ৳ " + totalPrice);
        alert.showAndWait();
    }

    private void showUpdateSuccessAlert(Medicine medicine, int newQuantity) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Successful");
        alert.setHeaderText("Medicine Updated");
        alert.setContentText("The medicine \"" + medicine.getBrandName() + "\" was updated successfully with quantity: "
                + newQuantity);
        alert.showAndWait();
    }



    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Failed to Add Item");
        alert.setContentText("There was an error while adding the item to the inventory.");
        alert.showAndWait();
    }

    // Alert for invalid input
    private void showInvalidInputAlert(String field) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Invalid Input");
        errorAlert.setHeaderText("Error");
        errorAlert.setContentText("Please enter a valid number for the " + field + ".");
        errorAlert.showAndWait();
    }

    private void showNoSelectionAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Item Selected");
        alert.setContentText("Please select a medicine from the table before adding it to the inventory.");
        alert.showAndWait();
    }
}
