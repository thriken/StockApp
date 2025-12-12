package com.win7e.yuan.stock.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String DEFAULT_BASE_URL = "https://yuan.023sc.net/api/";

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    public static ApiService getApiService(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String baseUrl = sharedPreferences.getString("base_url", DEFAULT_BASE_URL);

        // Ensure the base URL ends with a slash, as required by Retrofit.
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}
