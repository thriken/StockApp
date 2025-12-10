package com.win7e.yuan.stock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.win7e.yuan.stock.model.AppInfoResponse;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);

        fetchAndCacheAppInfo(); // Fetch app info on startup

        if (savedInstanceState == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("stock_prefs", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);

            if (token != null && !token.isEmpty()) {
                // Token exists, go directly to MainFragment
                String name = sharedPreferences.getString("name", "");
                String role = sharedPreferences.getString("role", "");
                String baseId = sharedPreferences.getString("base_id", "");

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("role", role);
                bundle.putString("base_id", baseId);

                MainFragment mainFragment = new MainFragment();
                mainFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mainFragment)
                        .commit();
            } else {
                // No token, go to LoginFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        }
    }

    private void fetchAndCacheAppInfo() {
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getAppInfo("appname").enqueue(new Callback<AppInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<AppInfoResponse> call, @NonNull Response<AppInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    AppInfoResponse.Data data = response.body().getData();
                    if (data != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("stock_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("app_name", data.getAppName());
                        editor.putString("app_version", data.getVersion());
                        editor.apply();

                        // Also update the Activity title
                        setTitle(data.getAppName());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppInfoResponse> call, @NonNull Throwable t) {
                // Silently fail, old cached value or default text will be used.
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
            if (System.currentTimeMillis() - lastBackPressTime < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                lastBackPressTime = System.currentTimeMillis();
            }
        }
    }
}
