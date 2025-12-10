package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class AppInfoResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    // Getters
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    // Inner Data class
    public static class Data {
        @SerializedName("app_name")
        private String appName;

        @SerializedName("version")
        private String version;

        @SerializedName("description")
        private String description;

        // Getters
        public String getAppName() {
            return appName;
        }

        public String getVersion() {
            return version;
        }

    }
}
