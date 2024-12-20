package com.pharmacy.org.pharmacy;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    // Database connection variable
    private Connection sqliteConnection;

    @Override
    public void start(Stage stage) {
        try {
            // Connect to SQLite database (create if it doesn't exist)
            String dbUrl = "jdbc:sqlite:C:/Users/siddi/Desktop/CookingVideo/JavaProject/Pharmacy/src/main/resources/userbd.sqlite"; // Provide correct path to your database
            sqliteConnection = DriverManager.getConnection(dbUrl);

            if (sqliteConnection != null) {
                System.out.println("Connected to SQLite database.");
            } else {
                System.err.println("Failed to connect to SQLite database.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQLite connection error.");
            System.exit(1); // Exit the application if SQLite cannot connect
        }

        // Initialize JavaFX GUI
        SceneManager.setPrimaryStage(stage);
        SceneManager.loadScene("main-view.fxml", "Login");
    }

    @Override
    public void stop() {
        // Close SQLite connection when the application is closed
        try {
            if (sqliteConnection != null && !sqliteConnection.isClosed()) {
                sqliteConnection.close();
                System.out.println("SQLite database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to close SQLite connection.");
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
