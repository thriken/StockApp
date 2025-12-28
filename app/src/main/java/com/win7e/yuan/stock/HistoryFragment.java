package com.win7e.yuan.stock;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.win7e.yuan.stock.model.DropdownOptionsResponse;
import com.win7e.yuan.stock.model.HistoryRecord;
import com.win7e.yuan.stock.model.HistoryResponse;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private Button startDateButton, endDateButton, queryButton;
    private EditText packageCodeEditText;
    private Spinner operationTypeSpinner, baseSpinner;
    private RecyclerView historyRecyclerView;
    private HistoryListAdapter adapter;
    private final List<HistoryRecord> historyRecords = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Map<String, String> operationTypeNames = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation(view);
        fetchInitialFilters();
    }

    private void initializeViews(View view) {
        startDateButton = view.findViewById(R.id.button_start_date);
        endDateButton = view.findViewById(R.id.button_end_date);
        queryButton = view.findViewById(R.id.button_query_history);
        packageCodeEditText = view.findViewById(R.id.edit_text_package_code_filter);
        operationTypeSpinner = view.findViewById(R.id.spinner_operation_type_filter);
        baseSpinner = view.findViewById(R.id.spinner_base_filter);
        historyRecyclerView = view.findViewById(R.id.recycler_view_history);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new HistoryListAdapter(historyRecords, operationTypeNames);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        startDateButton.setOnClickListener(v -> showDatePickerDialog(startDateButton));
        endDateButton.setOnClickListener(v -> showDatePickerDialog(endDateButton));
        queryButton.setOnClickListener(v -> performQuery());
    }

    private void setupBottomNavigation(View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                NavHostFragment.findNavController(this).popBackStack(R.id.mainFragment, false);
                return true;
            } else if (itemId == R.id.navigation_scan) {
                NavHostFragment.findNavController(this).navigate(R.id.action_historyFragment_to_scanFragment);
                return true;
            } else if (itemId == R.id.navigation_history) {
                return true; // Already here
            } else if (itemId == R.id.navigation_logout) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).forceLogout("您已成功退出登录");
                }
                return true;
            }
            return false;
        });
    }

    private void showDatePickerDialog(Button dateButton) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            dateButton.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void fetchInitialFilters() {
        performQuery(true);
    }

    private void performQuery() {
        performQuery(false);
    }

    private void performQuery(boolean isInitial) {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        Map<String, String> options = new HashMap<>();
        if (!isInitial) {
            if (!startDateButton.getText().toString().equals("开始日期")) {
                options.put("start_date", startDateButton.getText().toString());
            }
            if (!endDateButton.getText().toString().equals("结束日期")) {
                options.put("end_date", endDateButton.getText().toString());
            }
            if (!packageCodeEditText.getText().toString().isEmpty()) {
                options.put("package_code", packageCodeEditText.getText().toString());
            }
            if (operationTypeSpinner.getSelectedItemPosition() > 0) {
                DropdownOptionsResponse.Option selected = (DropdownOptionsResponse.Option) operationTypeSpinner.getSelectedItem();
                options.put("operation_type", selected.getValue());
            }
            if (baseSpinner.getVisibility() == View.VISIBLE && baseSpinner.getSelectedItemPosition() > 0) {
                DropdownOptionsResponse.Option selected = (DropdownOptionsResponse.Option) baseSpinner.getSelectedItem();
                options.put("base_id", selected.getValue());
            }
        }

        apiService.getHistory(token, options).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<HistoryResponse> call, @NonNull Response<HistoryResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    HistoryResponse.Data data = response.body().getData();
                    if (isInitial && data.getFilters() != null) {
                        updateFilters(data.getFilters());
                    }
                    historyRecords.clear();
                    if(data.getRecords() != null){
                        historyRecords.addAll(data.getRecords());
                    }
                    adapter.notifyDataSetChanged();
                } else if(isAdded()){
                    Toast.makeText(getContext(), "查询失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HistoryResponse> call, @NonNull Throwable t) {
                if(isAdded()) Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFilters(HistoryResponse.Filters filters) {
        if (filters.getOperationTypes() != null) {
            operationTypeNames = filters.getOperationTypes().stream()
                .collect(Collectors.toMap(DropdownOptionsResponse.Option::getValue, DropdownOptionsResponse.Option::getLabel));
            adapter.updateOperationTypeNames(operationTypeNames);
            
            List<DropdownOptionsResponse.Option> opTypes = new ArrayList<>();
            opTypes.add(createHintOption("所有操作类型"));
            opTypes.addAll(filters.getOperationTypes());
            ArrayAdapter<DropdownOptionsResponse.Option> opAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opTypes);
            operationTypeSpinner.setAdapter(opAdapter);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        String userRole = prefs.getString("role", "");

        if ("admin".equals(userRole) && filters.getBases() != null && !filters.getBases().isEmpty()) {
            baseSpinner.setVisibility(View.VISIBLE);
            List<DropdownOptionsResponse.Option> bases = new ArrayList<>();
            bases.add(createHintOption("所有基地"));
            bases.addAll(filters.getBases());
            ArrayAdapter<DropdownOptionsResponse.Option> baseAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, bases);
            baseSpinner.setAdapter(baseAdapter);
        } else {
            baseSpinner.setVisibility(View.GONE);
        }
    }

    private DropdownOptionsResponse.Option createHintOption(String hint) {
        DropdownOptionsResponse.Option option = new DropdownOptionsResponse.Option();
        option.setLabel(hint);
        return option;
    }
}
