package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class ScanResponse {
    private int code;
    private String message;
    private long timestamp;
    private Data data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public String getMsg() { return message; } // Added for compatibility
    public Data getData() { return data; }

    public static class Data {
        private int id;
        @SerializedName("package_code")
        private String packageCode;
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
        private String specification;
        @SerializedName("entry_date")
        private String entryDate;

        // --- Getters ---
        public int getId() { return id; }
        public String getPackageCode() { return packageCode; }
        public String getGlassName() { return glassName; }
        public int getPieces() { return pieces; }
        public int getQuantity() { return quantity; }
        public String getCurrentRackCode() { return currentRackCode; }
        public int getCurrentRackId() { return currentRackId; }
        public String getCurrentAreaType() { return currentAreaType; }
        public String getBaseName() { return baseName; }
        public String getStatus() { return status; }
        public String getSpecification() { return specification; }
        public String getEntryDate() { return entryDate; }
    }
}
