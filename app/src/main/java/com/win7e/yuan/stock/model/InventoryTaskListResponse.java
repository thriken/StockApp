package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InventoryTaskListResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<InventoryTask> data;

    // Getters
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<InventoryTask> getData() {
        return data;
    }
}
