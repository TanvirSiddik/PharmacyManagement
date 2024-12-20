package com.pharmacy.org.pharmacy.Controllers;

import com.pharmacy.org.pharmacy.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static DataBase.DatabaseConnection.isLoggedin;

public class MainController {

    @FXML
    private TextField fieldUserName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label labelUserName;
    @FXML
    private Label errorMessageLabel; // Label for error messages

    @FXML
    public void userlogin() {
        // Validate user input
        String userName = fieldUserName.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Username and password must not be empty.");
            errorMessageLabel.setStyle("-fx-text-fill: red;"); // Show error in red
            return;
        }

        // Attempt login and validate credentials
        boolean isValid = isLoggedin(userName, password);

        if (isValid) {
            labelUserName.setText("Welcome, " + userName + "!");
            errorMessageLabel.setText(""); // Clear any previous error messages
            passwordField.clear();
            fieldUserName.clear();
            SceneManager.loadScene("dashboard-view.fxml", "Dashboard");
        } else {
            labelUserName.setText("");
            errorMessageLabel.setText("Invalid username or password.");
            errorMessageLabel.setStyle("-fx-text-fill: red;"); // Show error in red
        }
    }

    public void userSignup() {
        // Proceed to the sign-up scene
        SceneManager.loadScene("signup-view.fxml", "SignUp");
    }
}
