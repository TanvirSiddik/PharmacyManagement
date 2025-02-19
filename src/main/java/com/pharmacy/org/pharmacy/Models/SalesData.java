package com.pharmacy.org.pharmacy.Models;

public class SalesData {
    private final String date;
    private final double amount;

    public SalesData(String date, double amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }
}
