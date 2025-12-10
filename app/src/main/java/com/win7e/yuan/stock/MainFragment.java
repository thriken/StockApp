package com.win7e.yuan.stock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.util.HashMap;
import java.util.Map;

public class MainFragment extends Fragment {

    private TextView userInfo;
    private TextView appTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        userInfo = view.findViewById(R.id.user_info);
        appTitle = view.findViewById(R.id.app_title);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set app title from cache
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String appName = sharedPreferences.getString("app_name", getString(R.string.InventorySystem));
        appTitle.setText(appName);

        Map<String, String> baseNames = new HashMap<>();
        baseNames.put("1", "信义基地");
        baseNames.put("2", "新丰基地");
        baseNames.put("3", "金鱼基地");

        Map<String, String> roles = new HashMap<>();
        roles.put("admin", "超级管理员");
        roles.put("manager", "库管");
        roles.put("operator", "操作员");
        roles.put("viewer", "查看者");

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String role = getArguments().getString("role");
            String baseId = getArguments().getString("base_id");
            String userInfoText = "[" + baseNames.get(baseId) + "] " + roles.get(role) + " " + name;
            userInfo.setText(userInfoText);

            // Show inventory check card for specific roles
            if ("manager".equals(role) || "admin".equals(role)) {
                CardView inventoryCheckCard = view.findViewById(R.id.card_inventory_check);
                inventoryCheckCard.setVisibility(View.VISIBLE);
                inventoryCheckCard.setOnClickListener(v -> {
                    InventoryCheckFragment inventoryCheckFragment = new InventoryCheckFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, inventoryCheckFragment)
                            .addToBackStack(null)
                            .commit();
                });
            }
        }

        CardView scanCard = view.findViewById(R.id.card_scan);
        scanCard.setOnClickListener(v -> {
            ScanFragment scanFragment = new ScanFragment(); 
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, scanFragment)
                    .addToBackStack(null)
                    .commit();
        });

        CardView logoutCard = view.findViewById(R.id.card_logout);
        logoutCard.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("退出登录")
                    .setMessage("您确定要退出登录吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        // Clear all stored user data
                        SharedPreferences prefs = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
                        prefs.edit().clear().apply();

                        // Navigate back to the LoginFragment
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new LoginFragment())
                                .commit();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
}
