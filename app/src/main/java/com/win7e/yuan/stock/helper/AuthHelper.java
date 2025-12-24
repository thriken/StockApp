package com.win7e.yuan.stock.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthHelper {

    public static final String PREFS_NAME = "stock_prefs";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_EXPIRE_TIME = "expire_time"; // Renamed from KEY_EXPIRES_AT

    /**
     * Checks if the current user session is valid.
     * A session is valid if a token exists and the current time is before the expiration time.
     *
     * @param context The application context to access SharedPreferences.
     * @return true if the session is valid, false otherwise.
     */
    public static boolean isSessionValid(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String token = prefs.getString(KEY_TOKEN, null);
        long expireTime = prefs.getLong(KEY_EXPIRE_TIME, 0); // Use the correct key

        if (token == null || token.isEmpty()) {
            return false; // No token, session is invalid.
        }

        // The expireTime is in seconds, System.currentTimeMillis() is in milliseconds.
        // We need to convert them to the same unit for comparison.
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        return currentTimeInSeconds < expireTime;
    }
}
