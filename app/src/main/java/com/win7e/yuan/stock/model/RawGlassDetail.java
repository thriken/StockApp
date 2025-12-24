package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the detailed inventory summary of a specific Raw Glass type.
 */
public class RawGlassDetail {

    @SerializedName("glass_type")
    private GlassTypeInfo glassType;

    @SerializedName("inventory_summary")
    private InventorySummary inventorySummary;

    @SerializedName("base_distribution")
    private List<BaseDistribution> baseDistribution;

    public GlassTypeInfo getGlassType() { return glassType; }
    public InventorySummary getInventorySummary() { return inventorySummary; }
    public List<BaseDistribution> getBaseDistribution() { return baseDistribution; }

    public static class GlassTypeInfo {
        private int id;
        private String name;
        private Attributes attributes;

        public int getId() { return id; }
        public String getName() { return name; }
        public Attributes getAttributes() { return attributes; }
    }

    public static class Attributes {
        private String brand;
        private double thickness;
        private String color;

        public String getBrand() { return brand; }
        public double getThickness() { return thickness; }
        public String getColor() { return color; }
    }

    public static class InventorySummary {
        @SerializedName("total_packages")
        private int totalPackages;
        @SerializedName("total_pieces")
        private int totalPieces;
        @SerializedName("total_racks_used")
        private int totalRacksUsed;
        @SerializedName("total_quantity")
        private double totalQuantity; // Represents total area

        public int getTotalPackages() { return totalPackages; }
        public int getTotalPieces() { return totalPieces; }
        public int getTotalRacksUsed() { return totalRacksUsed; }
        public double getTotalQuantity() { return totalQuantity; } // Getter for total area
    }

    public static class BaseDistribution {
        @SerializedName("base_name")
        private String baseName;
        @SerializedName("total_packages")
        private int totalPackages;
        private List<RackDistribution> racks;

        public String getBaseName() { return baseName; }
        public int getTotalPackages() { return totalPackages; }
        public List<RackDistribution> getRacks() { return racks; }
    }

    public static class RackDistribution {
        @SerializedName("rack_name")
        private String rackName;
        @SerializedName("package_count")
        private int packageCount;

        public String getRackName() { return rackName; }
        public int getPackageCount() { return packageCount; }
    }
}
