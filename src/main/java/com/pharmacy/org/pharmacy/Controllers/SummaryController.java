package com.pharmacy.org.pharmacy.Controllers;

import DataBase.DatabaseConnection;
import com.pharmacy.org.pharmacy.Models.SalesData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class SummaryController {

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label totalSalesLabel;

    @FXML
    private LineChart<String, Number> salesChart;

    @FXML
    private TableView<SalesData> salesTable;

    @FXML
    private TableColumn<SalesData, String> dateColumn;

    @FXML
    private TableColumn<SalesData, Double> salesAmountColumn;

    private final ObservableList<SalesData> salesDataList = FXCollections.observableArrayList();
    static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    @FXML
    public void initialize() {
        // Set up the table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        salesAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Set default date range to the last 7 days
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today.minusDays(6));
        endDatePicker.setValue(today);

        // Load data for the default range
        loadSalesData(startDatePicker.getValue(), endDatePicker.getValue());
    }

    @FXML
    public void onDateRangeSelected() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null && !startDate.isAfter(endDate)) {
            loadSalesData(startDate, endDate);
        } else {
            totalSalesLabel.setText("Invalid date range selected.");
        }
    }

    private void loadSalesData(LocalDate startDate, LocalDate endDate) {
        salesDataList.clear();
        salesChart.getData().clear();

        String query = "SELECT DATE(purchase_date) AS date, SUM(purchase_amount) AS total_sales " +
                "FROM purchases WHERE DATE(purchase_date) BETWEEN ? AND ? GROUP BY DATE(purchase_date)";
        try (Connection connection = DatabaseConnection.con();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, startDate.toString());
            statement.setString(2, endDate.toString());

            ResultSet resultSet = statement.executeQuery();

            double totalSales = 0;
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales Trend");

            while (resultSet.next()) {
                String date = resultSet.getString("date");
                double salesAmount = resultSet.getDouble("total_sales");

                totalSales += salesAmount;
                salesDataList.add(new SalesData(date, salesAmount));
                series.getData().add(new XYChart.Data<>(date, salesAmount));
            }

            salesTable.setItems(salesDataList);
            salesChart.getData().add(series);
            totalSalesLabel.setText("Total Sales: ৳" + decimalFormat.format(totalSales));

        } catch (SQLException e) {
            totalSalesLabel.setText("Error loading sales data.");
            e.printStackTrace();
        }
    }
}

    // Inner class for table view data representation

