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
import androidx.navigation.fragment.NavHostFragment;

import com.win7e.yuan.stock.helper.AuthHelper;

import java.util.HashMap;
import java.util.Map;

public class MainFragment extends BaseFragment {

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

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(AuthHelper.PREFS_NAME, Context.MODE_PRIVATE);
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

            if ("manager".equals(role) || "admin".equals(role)) {
                CardView inventoryCheckCard = view.findViewById(R.id.card_inventory_check);
                inventoryCheckCard.setVisibility(View.VISIBLE);
                inventoryCheckCard.setOnClickListener(v -> navigateToModule(R.id.action_mainFragment_to_inventoryCheckFragment));
            }
        }

        view.findViewById(R.id.card_scan).setOnClickListener(v -> navigateToModule(R.id.action_mainFragment_to_scanFragment));
        view.findViewById(R.id.card_raw_glass_query).setOnClickListener(v -> navigateToModule(R.id.action_mainFragment_to_raw_glass_nav_graph));

        view.findViewById(R.id.card_logout).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("退出登录")
                    .setMessage("您确定要退出登录吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).forceLogout("您已成功退出登录");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    private void navigateToModule(int actionId) {
        if (getContext() == null) return;

        if (AuthHelper.isSessionValid(getContext())) {
            NavHostFragment.findNavController(this).navigate(actionId);
        } else {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).forceLogout("会话已过期，请重新登录");
            }
        }
    }
}
