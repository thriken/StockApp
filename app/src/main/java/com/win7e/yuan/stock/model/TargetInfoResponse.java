package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class TargetInfoResponse {
    private int code;
    private String message;
    private long timestamp;
    private TargetData data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public TargetData getData() { return data; }

    public static class TargetData {
        @SerializedName("target_rack")
        private TargetRackInfo targetRack;
        private String operation_type;
        private String operation_name;

        public TargetRackInfo getTargetRack() { return targetRack; }
        public String getOperationType() { return operation_type; }
        public String getOperationName() { return operation_name; }
    }

    public static class TargetRackInfo {
        private String code;
        private String name;
        private String area_type;
        private String area_type_name;
        private String status;

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getAreaType() { return area_type; }
        public String getAreaTypeName() { return area_type_name; }
        public String getStatus() { return status; }
    }
}
