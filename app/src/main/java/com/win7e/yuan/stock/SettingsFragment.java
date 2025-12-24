package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.win7e.yuan.stock.network.RetrofitClient;

public class SettingsFragment extends Fragment {

    private EditText apiUrlEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
        apiUrlEditText = view.findViewById(R.id.edit_text_api_url);
        Button saveButton = view.findViewById(R.id.button_save_settings);

        loadCurrentApiUrl();

        saveButton.setOnClickListener(v -> saveApiUrl());
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void loadCurrentApiUrl() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String currentUrl = sharedPreferences.getString(RetrofitClient.KEY_API_BASE_URL, RetrofitClient.DEFAULT_BASE_URL);
        apiUrlEditText.setText(currentUrl);
    }

    private void saveApiUrl() {
        String newUrl = apiUrlEditText.getText().toString().trim();
        if (!newUrl.isEmpty()) {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(RetrofitClient.KEY_API_BASE_URL, newUrl);
            editor.apply();

            // Invalidate the existing Retrofit client so it gets recreated with the new URL
            RetrofitClient.invalidate();

            Toast.makeText(getContext(), "API地址已保存", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).popBackStack(); // Go back to the previous screen
        } else {
            Toast.makeText(getContext(), "API地址不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
