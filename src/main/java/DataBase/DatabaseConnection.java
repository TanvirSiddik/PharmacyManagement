package DataBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/dotDatabase/Pharma.db";
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    public static String getAuthName;

    public static Connection con() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    public static boolean isLoggedin(String username, String password) {
        String sql = "SELECT * FROM userlogin WHERE username=? AND password=?";


        try (Connection connection = con();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            getAuthName = (resultSet.getString("username"));
            System.out.println(getAuthName);
            return resultSet.next();
        } catch (SQLException exception) {
            System.out.println("Database error: " + exception.getMessage());
            return false;
        }
    }

    // Method to insert a new user into the database
    public static String insertUser(String username, String password) {
        String sql = "INSERT INTO userlogin (username, password) VALUES (?, ?)";

        try (Connection connection = con();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                return "You're registered successfully!";
            } else {
                return "Registration failed!";
            }
        } catch (SQLException exception) {
            if (exception.getMessage().contains("UNIQUE constraint failed")) {
                return "Username already exists!";
            } else {
                System.out.println("Database error: " + exception.getMessage());
                return "Registration failed due to a database error!";
            }
        }
    }

    // **NEW** Method to execute SELECT queries
    public static ResultSet executeQuery(String query) {
        try (Connection connection = con();
             Statement statement = connection.createStatement()) {

            return statement.executeQuery(query);
        } catch (SQLException exception) {
            System.out.println("Error executing query: " + exception.getMessage());
            return null;
        }
    }

    // **NEW** Method to execute INSERT/UPDATE/DELETE queries
    public static int executeUpdate(String query) {
        try (Connection connection = con();
             Statement statement = connection.createStatement()) {

            return statement.executeUpdate(query);
        } catch (SQLException exception) {
            System.out.println("Error executing update: " + exception.getMessage());
            return 0;
        }
    }
}
