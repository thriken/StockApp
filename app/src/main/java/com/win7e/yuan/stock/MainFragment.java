package com.win7e.yuan.stock;

import android.app.AlertDialog;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        userInfo = view.findViewById(R.id.user_info);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        }

        CardView scanCard = view.findViewById(R.id.card_scan);
        scanCard.setOnClickListener(v -> {
            // Create a new instance every time to prevent crash on reuse
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
                        // For now, just exit the app. Later, we can clear token and go to login.
                        getActivity().finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
}
