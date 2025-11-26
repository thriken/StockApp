package com.win7e.yuan.stock.model;

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
        private User user;

        // Getters
        public String getToken() { return token; }
        public User getUser() { return user; }
    }
}
