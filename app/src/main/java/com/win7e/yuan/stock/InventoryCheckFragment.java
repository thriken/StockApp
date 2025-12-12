package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class InventoryCheckFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private InventoryTaskListAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view_inventory_tasks);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.text_view_empty);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Setup Toolbar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data
        fetchInventoryTasks();
    }

    private void fetchInventoryTasks() {
        showLoading(true);

        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) {
            Toast.makeText(requireContext(), "请先设置API地址", Toast.LENGTH_LONG).show();
            showEmptyView("请在设置中配置API地址");
            return;
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        String authToken = "Bearer " + token;

        apiService.getInventoryTasks(authToken, "list").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<InventoryTaskListResponse> call, @NonNull Response<InventoryTaskListResponse> response) {
                if (!isAdded()) return;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<InventoryTask> tasks = response.body().getData();
                    if (tasks != null && !tasks.isEmpty()) {
                        adapter = new InventoryTaskListAdapter(tasks, InventoryCheckFragment.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    } else {
                        showEmptyView("当前没有盘点任务");
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<InventoryTaskListResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                handleApiFailure(t);
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setText(message);
        emptyView.setVisibility(View.VISIBLE);
    }
}
