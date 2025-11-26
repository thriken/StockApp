package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class ScanResponse {
    private int code;
    private String message;
    private long timestamp;
    private Data data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    // This class now directly represents the flat "data" object from the server
    public static class Data {
        private int id;
        private String package_code;
        @SerializedName("glass_name")
        private String glassName;
        private int pieces;
        private int quantity;
        @SerializedName("current_rack_code")
        private String currentRackCode;
        @SerializedName("current_rack_id")
        private int currentRackId;
        @SerializedName("current_area_type")
        private String currentAreaType;
        @SerializedName("base_name")
        private String baseName;
        private String status;

        // --- Getters ---
        public int getId() { return id; }
        public String getPackageCode() { return package_code; }
        public String getGlassName() { return glassName; }
        public int getPieces() { return pieces; }
        public int getQuantity() { return quantity; }
        public String getCurrentRackCode() { return currentRackCode; }
        public int getCurrentRackId() { return currentRackId; }
        public String getCurrentAreaType() { return currentAreaType; }
        public String getBaseName() { return baseName; }
        public String getStatus() { return status; }
    }
}