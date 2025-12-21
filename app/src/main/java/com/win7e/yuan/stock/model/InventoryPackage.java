package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class InventoryPackage {
    @SerializedName("package_code")
    private String packageCode;
    @SerializedName("rack_code")
    private String rackCode;
    @SerializedName("rack_name")
    private String rackName;
    private int pieces; // System quantity
    private int quantity; // Checked quantity

    public String getPackageCode() { return packageCode; }
    public String getRackCode() { return rackCode; }
    public String getRackName() { return rackName; }
    public int getPieces() { return pieces; }
    public int getQuantity() { return quantity; }
}
