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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.win7e.yuan.stock.model.LoginRequest;
import com.win7e.yuan.stock.model.LoginResponse;
import com.win7e.yuan.stock.model.User;
import com.win7e.yuan.stock.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextPassword = view.findViewById(R.id.edit_text_password);
        buttonLogin = view.findViewById(R.id.button_login);
        ImageButton buttonSettings = view.findViewById(R.id.button_settings);

        buttonLogin.setOnClickListener(v -> loginUser());
        buttonSettings.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit());

        return view;
    }

    private void loginUser() {
        buttonLogin.setEnabled(false);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            buttonLogin.setEnabled(true);
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = RetrofitClient.getApiService().login(loginRequest);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (!isAdded()) {
                    return; // Fragment is detached, do nothing.
                }

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    // Login successful
                    Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    User user = response.body().getData().getUser();

                    // Save token and user info
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getData().getToken());
                    editor.putString("name", user.getName());
                    editor.putString("role", user.getRole());
                    editor.putString("base_id", user.getBaseId());
                    editor.apply();

                    // Navigate to MainFragment
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
                    // Login failed (API logic error or HTTP error)
                    String errorMessage = "登录失败，请重试。";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    buttonLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    return; // Fragment is detached, do nothing.
                }
                // Network error
                Toast.makeText(requireContext(), "网络错误，请检查您的网络连接。", Toast.LENGTH_SHORT).show();
                buttonLogin.setEnabled(true);
            }
        });
    }
}
