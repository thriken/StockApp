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

public class SettingsFragment extends Fragment {

    private EditText editTextServerUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextServerUrl = view.findViewById(R.id.edit_text_server_url);
        Button buttonSave = view.findViewById(R.id.button_save);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Setup Toolbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Load saved URL
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String currentUrl = sharedPreferences.getString("base_url", "https://yuan.win7e.com/api");
        editTextServerUrl.setText(currentUrl);

        // Save Button
        buttonSave.setOnClickListener(v -> {
            String newUrl = editTextServerUrl.getText().toString().trim();
            if (!newUrl.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("base_url", newUrl);
                editor.apply();
                Toast.makeText(getContext(), "Server URL saved!", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
