package com.pharmacy.org.pharmacy.Controllers;

import com.pharmacy.org.pharmacy.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import static DataBase.DatabaseConnection.isLoggedin;

public class MainController {
    @FXML
    public HBox errorHBox;
    public AnchorPane mainPane;
    @FXML
    private MFXTextField fieldUserName;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private Label errorMessageLabel; // Label for error messages
    public void userlogin() {
        // Validate user input
        errorHBox.setVisible(false);
        String userName = fieldUserName.getText().trim().toLowerCase();
        String password = passwordField.getText().trim().toLowerCase();

        if (userName.isEmpty() || password.isEmpty()) {
            errorHBox.setVisible(true);
            errorMessageLabel.setText("Username and password must not be empty.");
            return;
        }

        // Attempt login and validate credentials
        boolean isValid = isLoggedin(userName, password);

        if (isValid) {
            errorHBox.setVisible(false);
            errorMessageLabel.setText(""); // Clear any previous error messages
            passwordField.clear();
            fieldUserName.clear();
            SceneManager.loadScene("dashboard-view.fxml", "Dashboard");
        } else {
            errorHBox.setVisible(true);
            errorMessageLabel.setText("Invalid username or password.");
            errorMessageLabel.setStyle("-fx-text-fill: red;"); // Show error in red
        }
    }

    public void initialize() {
        errorHBox.setVisible(false);
    }


}
