package com.pharmacy.org.pharmacy.Controllers;

import com.pharmacy.org.pharmacy.SceneManager;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


import static DataBase.DatabaseConnection.isLoggedin;

public class MainController {
    public HBox topNavPane;
    double xOffset;
    double yOffset;

    public MFXFontIcon closeIcon;
    public MFXFontIcon minimizeIcon;
    @FXML
    public HBox errorHBox;
    public AnchorPane mainPane;
    @FXML
    private MFXTextField fieldUserName;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private Label errorMessageLabel; // Label for error messages


    @FXML
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

    public void userSignup() {
        // Proceed to the sign-up scene
        SceneManager.loadScene("signup-view.fxml", "SignUp");
    }
    public void initialize() {
        errorHBox.setVisible(false);
        customButtonAction();
        setupWindowDragging();


    }

    public void customButtonAction() {
        closeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.exit());
        minimizeIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> ((Stage) mainPane.getScene().getWindow()).setIconified(true));
    }

    public void setupWindowDragging() {

        // Get the current stage dynamically if not already initialized
        topNavPane.setOnMousePressed(event -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            if (stage == null) {
                stage = (Stage) mainPane.getScene().getWindow();
            }
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        topNavPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            if (stage != null) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });
    }
}
