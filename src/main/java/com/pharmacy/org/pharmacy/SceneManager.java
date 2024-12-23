package com.pharmacy.org.pharmacy;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    // Set the primary stage (called once at the application startup)
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    // Method to load a new scene
    public static void loadScene(String fxmlFile, String title) {
        try {

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));

            if (loader.getLocation() == null) {
                throw new IllegalStateException("FXML file not found: " + fxmlFile);
            }

            Parent root = loader.load(); // This will throw an IOException if the FXML file is not found

            // Create a new scene
            Scene scene = new Scene(root);

            // Set the scene to the primary stage
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            // Print detailed error message
            System.out.println("Failed to load scene: " + fxmlFile);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
