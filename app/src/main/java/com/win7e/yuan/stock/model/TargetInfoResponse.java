package com.win7e.yuan.stock.model;

public class TargetInfoResponse {
    private int code;
    private String message;
    private long timestamp;
    private TargetInfo data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public TargetInfo getData() {
        return data;
    }
}
