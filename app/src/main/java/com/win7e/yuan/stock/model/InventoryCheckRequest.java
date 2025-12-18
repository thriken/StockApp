package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class InventoryCheckRequest {
    private final String action;
    private final int task_id;
    private final String package_code;
    private final String rack_code;
    @SerializedName("check_quantity")
    private final int quantity;

    public InventoryCheckRequest(String action, int taskId, String packageCode, String rackCode, int quantity) {
        this.action = action;
        this.task_id = taskId;
        this.package_code = packageCode;
        this.rack_code = rackCode;
        this.quantity = quantity;
    }
}
