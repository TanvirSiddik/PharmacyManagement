package com.pharmacy.org.pharmacy.Models;

public class Medicine {
    private final String brandName;
    private final String genericName;
    private final String strength;
    private final String manufacture;
    private final String packageContainer;

    public Medicine(String brandName, String genericName, String strength, String manufacture, String packageContainer) {
        this.brandName = brandName;
        this.genericName = genericName;
        this.strength = strength;
        this.manufacture = manufacture;
        this.packageContainer = packageContainer;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getGenericName() {
        return genericName;
    }

    public String getStrength() {
        return strength;
    }

    public String getManufacture() {
        return manufacture;
    }

    public String getPackageContainer() {
        return packageContainer;
    }
}
