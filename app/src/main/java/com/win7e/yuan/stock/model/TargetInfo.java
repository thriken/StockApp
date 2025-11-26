package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class TargetInfo {
    @SerializedName("rack_code")
    private String rackCode;

    @SerializedName("rack_name")
    private String rackName;

    @SerializedName("area_type")
    private String areaType;

    @SerializedName("base_name")
    private String baseName;

    @SerializedName("transaction_type")
    private String operationType;

    @SerializedName("transaction_name")
    private String operationName;

    // Getters and Setters
    public String getRackCode() {
        return rackCode;
    }

    public void setRackCode(String rackCode) {
        this.rackCode = rackCode;
    }

    public String getRackName() {
        return rackName;
    }

    public void setRackName(String rackName) {
        this.rackName = rackName;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
