package com.pharmacy.org.pharmacy.Controllers;

import DataBase.DatabaseConnection;
import com.pharmacy.org.pharmacy.Models.InventoryItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.*;

public class InventoryController {

    public TextField searchField;
    public Button searchButton;
    public TableView<InventoryItem> inventoryTable;
    public Button updateButton;
    public Button deleteButton;

    // Observable list to hold inventory items
    private final ObservableList<InventoryItem> inventory = FXCollections.observableArrayList();
    public TableColumn brandNameColumn;
    public TableColumn genericNameColumn;
    public TableColumn strengthColumn;
    public TableColumn manufacturerColumn;
    public TableColumn priceColumn;
    public TableColumn quantityColumn;

    public void initialize() {
        // Configure the inventoryTable
        TableColumn<InventoryItem, String> brandNameColumn = new TableColumn<>("Brand Name");
        brandNameColumn.setCellValueFactory(new PropertyValueFactory<>("brandName"));

        TableColumn<InventoryItem, String> genericNameColumn = new TableColumn<>("Generic Name");
        genericNameColumn.setCellValueFactory(new PropertyValueFactory<>("genericName"));

        TableColumn<InventoryItem, String> strengthColumn = new TableColumn<>("Strength");
        strengthColumn.setCellValueFactory(new PropertyValueFactory<>("strength"));

        TableColumn<InventoryItem, String> manufacturerColumn = new TableColumn<>("Manufacturer");
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        TableColumn<InventoryItem, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<InventoryItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Adding columns to the table
        inventoryTable.getColumns().addAll(brandNameColumn, genericNameColumn, strengthColumn, manufacturerColumn, priceColumn, quantityColumn);
        inventoryTable.setItems(inventory);

        // Load data into the table
        loadInventoryData();
    }

    private void loadInventoryData() {
        String query = "SELECT brand_name, generic_name, strength, manufacturer, price, quantity FROM Inventory";
        try (Connection conn = DatabaseConnection.con();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String brandName = rs.getString("brand_name");
                String genericName = rs.getString("generic_name");
                String strength = rs.getString("strength");
                String manufacturer = rs.getString("manufacturer");
                String price = rs.getString("price");
                int quantity = rs.getInt("quantity");

                inventory.add(new InventoryItem(brandName, genericName, strength, manufacturer, price, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error loading inventory data.");
        }
    }

    public void onSearch(ActionEvent actionEvent) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadInventoryData(); // Reload all items if search is empty
        } else {
            ObservableList<InventoryItem> filteredList = FXCollections.observableArrayList();
            String searchQuery = "SELECT * FROM Inventory WHERE brand_name LIKE ?";
            try (Connection conn = DatabaseConnection.con();
                 PreparedStatement stmt = conn.prepareStatement(searchQuery)) {

                stmt.setString(1, "%" + query + "%");
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String brandName = rs.getString("brand_name");
                    String genericName = rs.getString("generic_name");
                    String strength = rs.getString("strength");
                    String manufacturer = rs.getString("manufacturer");
                    String price = rs.getString("price");
                    int quantity = rs.getInt("quantity");

                    filteredList.add(new InventoryItem(brandName, genericName, strength, manufacturer, price, quantity));
                }
                inventoryTable.setItems(filteredList);
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Error searching for inventory.");
            }
        }
    }


    public void onUpdate(ActionEvent actionEvent) {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Create a custom dialog with TextFields for each property
            Dialog<InventoryItem> dialog = new Dialog<>();
            dialog.setTitle("Update Medicine");
            dialog.setHeaderText("Update details for: " + selected.getBrandName());

            // Create the grid pane to arrange the input fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Create TextFields for each property
            TextField brandNameField = new TextField(selected.getBrandName());
            TextField genericNameField = new TextField(selected.getGenericName());
            TextField strengthField = new TextField(selected.getStrength());
            TextField manufacturerField = new TextField(selected.getManufacturer());
            TextField priceField = new TextField(selected.getPrice());
            TextField quantityField = new TextField(String.valueOf(selected.getQuantity()));

            // Add labels and fields to the grid
            grid.add(new Label("Brand Name:"), 0, 0);
            grid.add(brandNameField, 1, 0);
            grid.add(new Label("Generic Name:"), 0, 1);
            grid.add(genericNameField, 1, 1);
            grid.add(new Label("Strength:"), 0, 2);
            grid.add(strengthField, 1, 2);
            grid.add(new Label("Manufacturer:"), 0, 3);
            grid.add(manufacturerField, 1, 3);
            grid.add(new Label("Price:"), 0, 4);
            grid.add(priceField, 1, 4);
            grid.add(new Label("Quantity:"), 0, 5);
            grid.add(quantityField, 1, 5);

            // Set the dialog's content to the grid
            dialog.getDialogPane().setContent(grid);

            // Add OK and Cancel buttons
            ButtonType okButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            // Convert the result when the OK button is pressed
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    // Validate price
                    double priceValue;
                    int quantityValue;
                    try {
                        priceValue = Double.parseDouble(priceField.getText());
                        quantityValue = Integer.parseInt(quantityField.getText());
                        if (priceValue <= 0) {
                            showErrorAlert("Price must be a positive number.");
                            return null;
                        }
                    } catch (NumberFormatException e) {
                        showErrorAlert("Invalid format for price or quantity.");
                        return null;
                    }

                    // Update the selected item
                    selected.setBrandName(brandNameField.getText());
                    selected.setGenericName(genericNameField.getText());
                    selected.setStrength(strengthField.getText());
                    selected.setManufacturer(manufacturerField.getText());
                    selected.setPrice(priceField.getText());
                    selected.setQuantity(quantityValue);

                    // Update the database
                    String updateQuery = "UPDATE Inventory SET brand_name = ?, generic_name = ?, strength = ?, manufacturer = ?, price = ?, quantity = ? WHERE brand_name = ?";
                    try (Connection conn = DatabaseConnection.con();
                         PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                        stmt.setString(1, selected.getBrandName());
                        stmt.setString(2, selected.getGenericName());
                        stmt.setString(3, selected.getStrength());
                        stmt.setString(4, selected.getManufacturer());
                        stmt.setString(5, selected.getPrice());
                        stmt.setInt(6, selected.getQuantity());
                        stmt.setString(7, selected.getBrandName());

                        stmt.executeUpdate();
                        inventoryTable.refresh(); // Refresh the table
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showErrorAlert("Error updating inventory.");
                    }
                }
                return null;
            });

            // Show the dialog
            dialog.showAndWait();
        } else {
            showErrorAlert("No item selected for update.");
        }
    }


    public void onDelete(ActionEvent actionEvent) {
        InventoryItem selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Delete confirmation
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Delete Confirmation");
            confirmation.setHeaderText("Are you sure you want to delete this item?");
            confirmation.setContentText("Brand: " + selected.getBrandName());

            if (confirmation.showAndWait().get() == ButtonType.OK) {
                // Delete from database
                String deleteQuery = "DELETE FROM Inventory WHERE brand_name = ?";
                try (Connection conn = DatabaseConnection.con();
                     PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setString(1, selected.getBrandName());
                    stmt.executeUpdate();
                    inventory.remove(selected); // Remove from the ObservableList
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorAlert("Error deleting item.");
                }
            }
        } else {
            showErrorAlert("No item selected for deletion.");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
