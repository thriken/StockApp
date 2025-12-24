package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private int code;
    private String message;
    private long timestamp;
    private LoginData data;

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public LoginData getData() { return data; }

    public static class LoginData {
        private String token;

        @SerializedName("expire_time")
        private long expireTime; // Unix timestamp in seconds

        private User user;

        // Getters
        public String getToken() { return token; }
        public long getExpireTime() { return expireTime; }
        public User getUser() { return user; }
    }
}
