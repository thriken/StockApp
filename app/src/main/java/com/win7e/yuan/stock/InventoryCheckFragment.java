package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.InventoryTaskListResponse;
import com.win7e.yuan.stock.model.InventoryTask;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCheckFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryTaskListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_check, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_inventory_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchInventoryTasks();
        return view;
    }

    private void fetchInventoryTasks() {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) {
            Toast.makeText(requireContext(), "请先设置API地址", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String authToken = "Bearer " + token;

        apiService.getInventoryTasks(authToken, "list").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<InventoryTaskListResponse> call, @NonNull Response<InventoryTaskListResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<InventoryTask> tasks = response.body().getData();
                    if (tasks != null && !tasks.isEmpty()) {
                        adapter = new InventoryTaskListAdapter(tasks);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "当前没有盘点任务", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "获取任务列表失败";
                    if (response.body() != null) {
                        errorMessage += ": " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<InventoryTaskListResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "网络错误，请检查连接", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
