package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Raw Glass type for selection, can be returned from both
 * fuzzy_search (as SelectionOption) and get_dropdown_options (as GlassType).
 */
public class RawGlass {

    private int id;

    @SerializedName("custom_id")
    private String customId;

    private String name;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("display_name")
    private String displayName;

    // These fields are only present in the fuzzy_search response
    private String brand;
    private String manufacturer;
    private String color;
    private double thickness;

    @SerializedName("has_inventory")
    private boolean hasInventory;

    @SerializedName("total_packages")
    private int totalPackages;

    // Getters
    public int getId() { return id; }
    public String getCustomId() { return customId; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getDisplayName() { return displayName; }
    public String getBrand() { return brand; }
    public String getManufacturer() { return manufacturer; }
    public String getColor() { return color; }
    public double getThickness() { return thickness; }
    public boolean hasInventory() { return hasInventory; }
    public int getTotalPackages() { return totalPackages; }
}
