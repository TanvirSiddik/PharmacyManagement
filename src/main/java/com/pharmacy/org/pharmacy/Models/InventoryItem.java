package com.pharmacy.org.pharmacy.Models;

public class InventoryItem {
    private String brandName;
    private String genericName;
    private String strength;
    private String manufacturer;
    private String price;
    private int quantity;


    public InventoryItem(String brandName, String genericName, String strength, String manufacturer, String price, int quantity) {
        this.brandName = brandName;
        this.genericName = genericName;
        this.strength = strength;
        this.manufacturer = manufacturer;
        this.price = price;
        this.quantity = quantity;
    }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }

    public String getStrength() { return strength; }
    public void setStrength(String strength) { this.strength = strength; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = this.price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
