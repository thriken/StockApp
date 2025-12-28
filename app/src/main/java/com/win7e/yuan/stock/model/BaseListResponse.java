package com.win7e.yuan.stock.model;

import java.util.List;

public class BaseListResponse {
    private int code;
    private String message;
    private List<Base> data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<Base> getData() { return data; }
}
