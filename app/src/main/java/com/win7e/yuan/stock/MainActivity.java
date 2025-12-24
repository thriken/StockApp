package com.win7e.yuan.stock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.win7e.yuan.stock.helper.AuthHelper;
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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        fetchAndCacheAppInfo(); // Fetch app info on startup

        if (savedInstanceState == null) {
            // Check session on initial startup
            if (AuthHelper.isSessionValid(this)) {
                // Token exists and is valid, go directly to mainFragment
                SharedPreferences sharedPreferences = getSharedPreferences(AuthHelper.PREFS_NAME, MODE_PRIVATE);
                String name = sharedPreferences.getString("name", "");
                String role = sharedPreferences.getString("role", "");
                String baseId = sharedPreferences.getString("base_id", "");

                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("role", role);
                bundle.putString("base_id", baseId);

                navController.navigate(R.id.mainFragment, bundle, 
                    new NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build());
            } else {
                // No valid session, start destination (loginFragment) will be used by default
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check session validity every time the app comes to the foreground
        // But only if the user is not already on the login screen.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.loginFragment) {
            if (!AuthHelper.isSessionValid(this)) {
                forceLogout("会话已过期，请重新登录");
            }
        }
    }

    public void forceLogout(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Selectively clear user data
        SharedPreferences prefs = getSharedPreferences(AuthHelper.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AuthHelper.KEY_TOKEN);
        editor.remove(AuthHelper.KEY_EXPIRE_TIME); // Use correct key
        editor.remove("name");
        editor.remove("role");
        editor.remove("base_id");
        editor.apply();

        // Navigate to login screen and clear back stack
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.loginFragment, null, new NavOptions.Builder()
            .setPopUpTo(R.id.nav_graph, true)
            .build());
    }

    private void fetchAndCacheAppInfo() {
        ApiService apiService = RetrofitClient.getApiService(this);
        apiService.getAppInfo("appname").enqueue(new Callback<AppInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<AppInfoResponse> call, @NonNull Response<AppInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    AppInfoResponse.Data data = response.body().getData();
                    if (data != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences(AuthHelper.PREFS_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("app_name", data.getAppName());
                        editor.putString("app_version", data.getVersion());
                        editor.apply();

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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.mainFragment) {
            if (System.currentTimeMillis() - lastBackPressTime < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
                lastBackPressTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }
}
