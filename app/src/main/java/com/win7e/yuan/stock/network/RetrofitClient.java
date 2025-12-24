package com.win7e.yuan.stock.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String KEY_API_BASE_URL = "api_base_url";
    public static final String DEFAULT_BASE_URL = "https://yuan.023sc.net/api/";

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    public static ApiService getApiService(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String baseUrl = sharedPreferences.getString(KEY_API_BASE_URL, DEFAULT_BASE_URL);

        if (baseUrl.isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Enhance TLS compatibility
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
        retrofit = null;
        currentBaseUrl = null;
    }
}
