package com.win7e.yuan.stock.model;

public class Package {
    private int id;
    private String package_code;
    private Dimensions dimensions;
    private Quantity quantity;
    private String entry_date;
    private int position_order;
    private GlassType glass_type;
    private RackInfo rack_info;
    private String status;
    private String status_name;
    private String created_at;
    private String updated_at;

    // Getters
    public int getId() { return id; }
    public String getPackageCode() { return package_code; }
    public Dimensions getDimensions() { return dimensions; }
    public Quantity getQuantity() { return quantity; }
    public String getEntryDate() { return entry_date; }
    public int getPositionOrder() { return position_order; }
    public GlassType getGlassType() { return glass_type; }
    public RackInfo getRackInfo() { return rack_info; }
    public String getStatus() { return status; }
    public String getStatusName() { return status_name; }
    public String getCreatedAt() { return created_at; }
    public String getUpdatedAt() { return updated_at; }

    public static class Dimensions {
        private double width;
        private double height;

        // Getters
        public double getWidth() { return width; }
        public double getHeight() { return height; }
    }

    public static class Quantity {
        private int pieces;
        private int quantity;

        // Getters
        public int getPieces() { return pieces; }
        public int getQuantity() { return quantity; }
    }

    public static class GlassType {
        private int id;
        private String custom_id;
        private String name;
        private String short_name;
        private String brand;
        private String manufacturer;
        private String color;
        private double thickness;
        private String silver_layers;
        private String substrate;
        private String transmittance;

        // Getters
        public int getId() { return id; }
        public String getCustomId() { return custom_id; }
        public String getName() { return name; }
        public String getShortName() { return short_name; }
        public String getBrand() { return brand; }
        public String getManufacturer() { return manufacturer; }
        public String getColor() { return color; }
        public double getThickness() { return thickness; }
        public String getSilverLayers() { return silver_layers; }
        public String getSubstrate() { return substrate; }
        public String getTransmittance() { return transmittance; }
    }

    public static class RackInfo {
        private int id;
        private String code;
        private String name;
        private String area_type;
        private int base_id;
        private String base_name;

        // Getters
        public int getId() { return id; }
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getAreaType() { return area_type; } // <-- Missing getter added
        public int getBaseId() { return base_id; }
        public String getBaseName() { return base_name; }
    }
}
