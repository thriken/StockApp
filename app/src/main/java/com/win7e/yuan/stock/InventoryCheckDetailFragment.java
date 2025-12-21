package com.win7e.yuan.stock;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.InventoryCheckDetailResponse;
import com.win7e.yuan.stock.model.InventoryCheckRequest;
import com.win7e.yuan.stock.model.InventoryPackage;
import com.win7e.yuan.stock.model.ScanResponse;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCheckDetailFragment extends BaseFragment {

    private int taskId;
    private TextView progressTextView, differenceTextView;
    private EditText packageCodeEditText, rackCodeEditText, quantityEditText;
    private Button submitButton;
    private RecyclerView packagesRecyclerView;
    private InventoryPackageListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt("taskId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_check_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
        initializeViews(view);
        setupFocusListeners();

        if (taskId != -1) {
            fetchTaskDetails();
        }

        submitButton.setOnClickListener(v -> submitInventoryCheck());
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void initializeViews(View view) {
        progressTextView = view.findViewById(R.id.text_view_progress);
        differenceTextView = view.findViewById(R.id.text_view_difference);
        packageCodeEditText = view.findViewById(R.id.edit_text_package_code);
        rackCodeEditText = view.findViewById(R.id.edit_text_rack_code);
        quantityEditText = view.findViewById(R.id.edit_text_quantity);
        submitButton = view.findViewById(R.id.button_submit);
        packagesRecyclerView = view.findViewById(R.id.recycler_view_packages);
        packagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupFocusListeners() {
        packageCodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                rackCodeEditText.requestFocus();
                return true;
            }
            return false;
        });

        rackCodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                quantityEditText.requestFocus();
                return true;
            }
            return false;
        });

        quantityEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                submitButton.performClick();
                return true;
            }
            return false;
        });
    }

    private void fetchTaskDetails() {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String authToken = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.getInventoryCheckDetail(authToken, "get", taskId).enqueue(new Callback<InventoryCheckDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<InventoryCheckDetailResponse> call, @NonNull Response<InventoryCheckDetailResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    updateUI(response.body().getData());
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to fetch details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<InventoryCheckDetailResponse> call, @NonNull Throwable t) {
                if (isAdded()) handleApiFailure(t);
            }
        });
    }

    private void submitInventoryCheck() {
        String packageCode = packageCodeEditText.getText().toString();
        String rackCode = rackCodeEditText.getText().toString();
        String quantityStr = quantityEditText.getText().toString();

        if (TextUtils.isEmpty(packageCode) || TextUtils.isEmpty(rackCode) || TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String authToken = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        InventoryCheckRequest request = new InventoryCheckRequest("scan", taskId, packageCode, rackCode, quantity);

        apiService.submitInventoryCheck(authToken, request).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScanResponse> call, @NonNull Response<ScanResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ScanResponse scanResponse = response.body();
                    Toast.makeText(getContext(), scanResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    if (scanResponse.getCode() == 200) {
                        // Success
                        fetchTaskDetails();
                        clearInputFields();
                        packageCodeEditText.requestFocus();
                    } else {
                        // API error, e.g. validation failed
                        handleApiError();
                    }
                } else {
                    // HTTP error
                    Toast.makeText(getContext(), "提交失败，请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScanResponse> call, @NonNull Throwable t) {
                if (isAdded()) handleApiFailure(t);
            }
        });
    }

    private void clearInputFields() {
        packageCodeEditText.setText("");
        rackCodeEditText.setText("");
        quantityEditText.setText("");
    }

    private void handleApiError() {
        // For now, we assume the package code is the most likely source of error.
        packageCodeEditText.requestFocus();
        packageCodeEditText.selectAll();
    }

    @Override
    protected void handleApiFailure(Throwable t) {
        Toast.makeText(getContext(), "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void updateUI(InventoryCheckDetailResponse.Data data) {
        if (data == null || data.getTask() == null) return;

        InventoryCheckDetailResponse.Task task = data.getTask();

        progressTextView.setText(String.format("已盘/总数\n%d / %d", task.getCheckedPackages(), task.getTotalPackages()));
        differenceTextView.setText(String.format("差异数\n%d", task.getDifferenceCount()));

        List<InventoryPackage> packages = data.getPackages();
        if (packages != null) {
            if (adapter == null) {
                adapter = new InventoryPackageListAdapter(packages);
                packagesRecyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(packages);
            }
        }
    }
}
