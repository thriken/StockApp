package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class HistoryRecord {

    private int id;

    @SerializedName("record_no")
    private String recordNo;

    @SerializedName("operation_type")
    private String operationType;

    @SerializedName("package_code")
    private String packageCode;

    @SerializedName("glass_name")
    private String glassName;

    @SerializedName("operation_quantity")
    private int operationQuantity;

    @SerializedName("before_quantity")
    private int beforeQuantity;

    @SerializedName("after_quantity")
    private int afterQuantity;

    @SerializedName("base_name")
    private String baseName;

    @SerializedName("operator_name")
    private String operatorName;

    @SerializedName("from_rack_code")
    private String fromRackCode;

    @SerializedName("to_rack_code")
    private String toRackCode;

    @SerializedName("operation_date")
    private String operationDate;

    @SerializedName("operation_time")
    private String operationTime;

    // Getters
    public int getId() { return id; }
    public String getRecordNo() { return recordNo; }
    public String getOperationType() { return operationType; }
    public String getPackageCode() { return packageCode; }
    public String getGlassName() { return glassName; }
    public int getOperationQuantity() { return operationQuantity; }
    public int getBeforeQuantity() { return beforeQuantity; }
    public int getAfterQuantity() { return afterQuantity; }
    public String getBaseName() { return baseName; }
    public String getOperatorName() { return operatorName; }
    public String getFromRackCode() { return fromRackCode; }
    public String getToRackCode() { return toRackCode; }
    public String getOperationDate() { return operationDate; }
    public String getOperationTime() { return operationTime; }
}
