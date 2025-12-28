package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class TransferRequest {

    @SerializedName("package_code")
    private final String packageCode;

    @SerializedName("target_base_id")
    private final int targetBaseId;

    public TransferRequest(String packageCode, int targetBaseId) {
        this.packageCode = packageCode;
        this.targetBaseId = targetBaseId;
    }
}
