package com.win7e.yuan.stock.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Collections;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String TAG = "RetrofitClient";
    public static final String KEY_API_BASE_URL = "api_base_url";
    public static final String DEFAULT_BASE_URL = "https://yuan.023sc.net/api/";

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    public static ApiService getApiService(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        
        // 1. Attempt to read saved configuration
        String baseUrl = sharedPreferences.getString(KEY_API_BASE_URL, "");

        // 2. If saved is empty (first run or cleared), use hardcoded default
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
            Log.d(TAG, "No saved API URL found, loading default: " + DEFAULT_BASE_URL);
        } else {
            Log.d(TAG, "Loading saved API URL: " + baseUrl);
        }

        // 3. Normalize URL
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        // 4. Singleton pattern with URL-change detection
        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            Log.i(TAG, "Initializing Retrofit with URL: " + baseUrl);
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(okhttp3.TlsVersion.TLS_1_2, okhttp3.TlsVersion.TLS_1_3)
                    .build();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.connectionSpecs(Collections.singletonList(spec));

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }

    public static void invalidate() {
        Log.w(TAG, "Retrofit instance invalidated. Will reload URL on next call.");
        retrofit = null;
        currentBaseUrl = null;
    }
}
