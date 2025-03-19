package com.pharmacy.org.pharmacy.Models;

public class Medicine {
    private String brandName;
    private String genericName;
    private String strength;
    private String manufacture;
    private String packageContainer;

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

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    public void setPackageContainer(String packageContainer) {
        this.packageContainer = packageContainer;
    }

    public String getManufacture() {
        return manufacture;
    }

    public String getPackageContainer() {
        return packageContainer;
    }
}
