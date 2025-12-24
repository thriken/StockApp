package com.win7e.yuan.stock.model;

/**
 * Represents the full response for a glass_type_summary API call.
 */
public class RawGlassDetailResponse {

    private int code;
    private String message;
    private long timestamp;
    private RawGlassDetail data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public RawGlassDetail getData() { return data; }
}
