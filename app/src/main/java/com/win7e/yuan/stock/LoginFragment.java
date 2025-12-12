package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.win7e.yuan.stock.model.AppInfoResponse;
import com.win7e.yuan.stock.model.LoginRequest;
import com.win7e.yuan.stock.model.LoginResponse;
import com.win7e.yuan.stock.model.User;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView appNameTextView;
    private TextView appVersionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextPassword = view.findViewById(R.id.edit_text_password);
        buttonLogin = view.findViewById(R.id.button_login);
        ImageButton buttonSettings = view.findViewById(R.id.button_settings);
        appNameTextView = view.findViewById(R.id.text_view_app_name);
        appVersionTextView = view.findViewById(R.id.text_view_app_version);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonSettings.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit());

        updateAppInfoFromCache();
        fetchAppInfo();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAppInfoFromCache();
    }

    private void updateAppInfoFromCache() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String appName = sharedPreferences.getString("app_name", getString(R.string.InventorySystem));
        String appVersion = sharedPreferences.getString("app_version", "");

        appNameTextView.setText(appName);
        if (!appVersion.isEmpty()) {
            appVersionTextView.setText(getString(R.string.app_version_format, appVersion));
        } else {
            appVersionTextView.setText("");
        }
    }

    private void fetchAppInfo() {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        apiService.getAppInfo("appname").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AppInfoResponse> call, @NonNull Response<AppInfoResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    AppInfoResponse.Data data = response.body().getData();
                    if (data != null) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("app_name", data.getAppName());
                        editor.putString("app_version", data.getVersion());
                        editor.apply();
                        updateAppInfoFromCache();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<AppInfoResponse> call, @NonNull Throwable t) { /* Silently fail */ }
        });
    }

    private void loginUser() {
        buttonLogin.setEnabled(false);

        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) {
            Toast.makeText(requireContext(), "请先设置API地址", Toast.LENGTH_LONG).show();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();
            buttonLogin.setEnabled(true);
            return;
        }

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            buttonLogin.setEnabled(true);
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    User user = response.body().getData().getUser();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getData().getToken());
                    editor.putString("name", user.getName());
                    editor.putString("role", user.getRole());
                    editor.putString("base_id", user.getBaseId());
                    editor.apply();

                    Bundle bundle = new Bundle();
                    bundle.putString("name", user.getName());
                    bundle.putString("role", user.getRole());
                    bundle.putString("base_id", user.getBaseId());

                    MainFragment mainFragment = new MainFragment();
                    mainFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, mainFragment)
                            .commit();
                } else {
                    handleApiError(response);
                    buttonLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                handleApiFailure(t);
                buttonLogin.setEnabled(true);
            }
        });
    }
}
