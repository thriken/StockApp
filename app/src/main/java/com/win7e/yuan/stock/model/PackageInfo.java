package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class PackageInfo {

    @SerializedName("package_code")
    private String packageCode;

    private Dimensions dimensions;

    private Quantity quantity;

    @SerializedName("rack_info")
    private RackInfo rackInfo;

    public String getPackageCode() { return packageCode; }
    public Dimensions getDimensions() { return dimensions; }
    public Quantity getQuantity() { return quantity; }
    public RackInfo getRackInfo() { return rackInfo; }

    public static class Dimensions {
        private float width;
        private float height;

        public float getWidth() { return width; }
        public float getHeight() { return height; }
    }

    public static class Quantity {
        private int pieces;

        public int getPieces() { return pieces; }
    }

    public static class RackInfo {
        @SerializedName("rack_name")
        private String rackName;

        public String getRackName() { return rackName; }
    }
}
