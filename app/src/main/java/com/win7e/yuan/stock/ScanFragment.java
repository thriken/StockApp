package com.win7e.yuan.stock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.win7e.yuan.stock.model.ScanRequest;
import com.win7e.yuan.stock.model.ScanResponse;
import com.win7e.yuan.stock.model.TargetInfo;
import com.win7e.yuan.stock.model.TargetInfoResponse;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends BaseFragment {

    // UI Elements
    private EditText editTextPackageId, editTextShelfId, editTextDetectedOperation, editTextQuantity;
    private Spinner spinnerOperationType;
    private Button buttonSubmit;
    private CheckBox checkBoxAllUse;
    private LinearLayout layoutPackageInfo;
    private TextView textViewPackageId, textViewGlassType, textViewPieces, textViewCurrentShelf, textViewStatus;

    // Logic
    private final Handler handler = new Handler();
    private Runnable packageApiCall, shelfApiCall;
    private String authToken;
    private ScanResponse.Data currentPackageInfo;
    private TargetInfo currentTargetInfo;
    private int tempQuantity = 0;

    private enum ScanTarget { PACKAGE, SHELF }
    private ScanTarget currentScanTarget;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    if (currentScanTarget == ScanTarget.PACKAGE) {
                        editTextPackageId.setText(result.getContents());
                    } else if (currentScanTarget == ScanTarget.SHELF) {
                        editTextShelfId.setText(result.getContents());
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        authToken = "Bearer " + sharedPreferences.getString("token", "");

        setupToolbar(view);
        setupSpinner();
        setupClickListeners();
        setupTextWatchers();
        setupBottomNavigation(view);
    }

    private void initializeViews(View view) {
        editTextPackageId = view.findViewById(R.id.edit_text_package_id);
        editTextShelfId = view.findViewById(R.id.edit_text_shelf_id);
        editTextDetectedOperation = view.findViewById(R.id.edit_text_detected_operation);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        spinnerOperationType = view.findViewById(R.id.spinner_operation_type);
        buttonSubmit = view.findViewById(R.id.button_submit);
        checkBoxAllUse = view.findViewById(R.id.checkbox_all_use);
        layoutPackageInfo = view.findViewById(R.id.layout_package_info);
        textViewPackageId = view.findViewById(R.id.text_view_package_id);
        textViewGlassType = view.findViewById(R.id.text_view_glass_type);
        textViewPieces = view.findViewById(R.id.text_view_pieces);
        textViewCurrentShelf = view.findViewById(R.id.text_view_current_shelf);
        textViewStatus = view.findViewById(R.id.text_view_status);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.operation_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperationType.setAdapter(adapter);
    }

    private void setupClickListeners() {
        Button buttonScanPackage = requireView().findViewById(R.id.button_scan_package);
        Button buttonScanShelf = requireView().findViewById(R.id.button_scan_shelf);
        buttonScanPackage.setOnClickListener(v -> {
            currentScanTarget = ScanTarget.PACKAGE;
            launchScanner();
        });
        buttonScanShelf.setOnClickListener(v -> {
            currentScanTarget = ScanTarget.SHELF;
            launchScanner();
        });
        buttonSubmit.setOnClickListener(v -> executeTransaction());

        checkBoxAllUse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                try {
                    tempQuantity = Integer.parseInt(editTextQuantity.getText().toString());
                } catch (NumberFormatException e) {
                    tempQuantity = 0;
                }
                editTextQuantity.setText("0");
                editTextQuantity.setEnabled(false);
            } else {
                editTextQuantity.setText(String.valueOf(tempQuantity));
                editTextQuantity.setEnabled(true);
            }
        });
    }

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("扫描二维码");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private void setupTextWatchers() {
        packageApiCall = () -> callApiForPackageInfo(editTextPackageId.getText().toString());
        shelfApiCall = () -> callApiForTargetInfo(editTextShelfId.getText().toString());

        editTextPackageId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(packageApiCall);
                resetPackageInfo();
            }
            @Override public void afterTextChanged(Editable s) {
                if (s.length() > 0) handler.postDelayed(packageApiCall, 1000);
            }
        });

        editTextShelfId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(shelfApiCall);
                resetTargetInfo();
            }
            @Override public void afterTextChanged(Editable s) {
                if (s.length() > 0) handler.postDelayed(shelfApiCall, 500);
            }
        });
    }

    private void callApiForPackageInfo(String packageCode) {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) { return; }

        apiService.getPackageInfo(authToken, "get_package_info", packageCode).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScanResponse> call, @NonNull Response<ScanResponse> response) {
                if (!isAdded()) return;
                layoutPackageInfo.setVisibility(View.VISIBLE);
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    currentPackageInfo = response.body().getData();
                    if (currentPackageInfo != null && currentPackageInfo.getPackageCode() != null) {
                        displayPackageInfo(true);
                        editTextShelfId.requestFocus();
                    } else {
                        displayPackageInfo(false);
                    }
                } else {
                    displayPackageInfo(false);
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScanResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                layoutPackageInfo.setVisibility(View.VISIBLE);
                displayPackageInfo(false);
                handleApiFailure(t);
            }
        });
    }

    private void callApiForTargetInfo(String rackCode) {
        if (currentPackageInfo == null) return;
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) { return; }

        String currentAreaType = currentPackageInfo.getCurrentAreaType();
        apiService.getTargetInfo(authToken, "get_target_info", rackCode, currentAreaType).enqueue(new Callback<TargetInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<TargetInfoResponse> call, @NonNull Response<TargetInfoResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    currentTargetInfo = response.body().getData();
                    if (currentTargetInfo != null && currentTargetInfo.getOperationName() != null) {
                        displayOperationInfo();
                        editTextQuantity.requestFocus();
                    } else {
                        handleApiError(response);
                    }
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TargetInfoResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                handleApiFailure(t);
            }
        });
    }

    private void executeTransaction() {
        if (currentPackageInfo == null || currentTargetInfo == null) {
            Toast.makeText(getContext(), "请先扫描包号和目标架号", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        if (apiService == null) {
            Toast.makeText(requireContext(), "请先设置API地址", Toast.LENGTH_LONG).show();
            return;
        }

        int quantity = 0;
        boolean allUse = checkBoxAllUse.isChecked();

        if (!allUse) {
            try {
                quantity = Integer.parseInt(editTextQuantity.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "数量错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String transactionType = currentTargetInfo.getOperationType();
        String packageCode = currentPackageInfo.getPackageCode();
        String targetRackCode = currentTargetInfo.getRackCode();

        ScanRequest request = new ScanRequest(packageCode, targetRackCode, quantity, transactionType, "安卓设备提交", allUse);

        apiService.executeTransaction(authToken, request).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScanResponse> call, @NonNull Response<ScanResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(getContext(), "操作成功!", Toast.LENGTH_LONG).show();
                    resetAll();
                } else {
                    handleApiError(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScanResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                handleApiFailure(t);
            }
        });
    }

    private void displayPackageInfo(boolean found) {
        if (found && currentPackageInfo != null) {
            textViewPackageId.setText("包号: " + currentPackageInfo.getPackageCode());
            textViewGlassType.setText("玻璃类型: " + currentPackageInfo.getGlassName());
            textViewPieces.setText("片数: " + currentPackageInfo.getPieces());
            textViewCurrentShelf.setText("当前架号: " + currentPackageInfo.getCurrentRackCode());
            textViewStatus.setText("状态: " + currentPackageInfo.getStatus());
        } else {
            textViewPackageId.setText("包信息: 未找到该包号或信息不全");
            textViewGlassType.setText("");
            textViewPieces.setText("");
            textViewCurrentShelf.setText("");
            textViewStatus.setText("");
        }
    }

   private void displayOperationInfo() {
        if (currentTargetInfo == null) return;
        String transactionName = currentTargetInfo.getOperationName();
        editTextDetectedOperation.setText(transactionName);
        selectSpinnerItemByValue(transactionName);

        if ("usage_out".equals(currentTargetInfo.getOperationType())) {
            checkBoxAllUse.setVisibility(View.VISIBLE);
        } else {
            checkBoxAllUse.setVisibility(View.GONE);
        }
        if (currentPackageInfo != null) {
            editTextQuantity.setText(String.valueOf(currentPackageInfo.getPieces()));
        }
    }


    private void selectSpinnerItemByValue(String value) {
        if (value == null) return;
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerOperationType.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (value.equals(adapter.getItem(position).toString())) {
                spinnerOperationType.setSelection(position);
                return;
            }
        }
    }

    private void resetPackageInfo() {
        currentPackageInfo = null;
        if (layoutPackageInfo != null) layoutPackageInfo.setVisibility(View.GONE);
        resetTargetInfo();
    }

    private void resetTargetInfo() {
        currentTargetInfo = null;
        editTextDetectedOperation.setText("");
        editTextQuantity.setText("");
        checkBoxAllUse.setChecked(false);
        checkBoxAllUse.setVisibility(View.GONE);
        selectSpinnerItemByValue("请选择操作类型");
    }

    private void resetAll(){
        resetPackageInfo();
        editTextPackageId.setText("");
        editTextShelfId.setText("");
        editTextPackageId.requestFocus();
    }

    private void setupBottomNavigation(View view) {
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                getParentFragmentManager().popBackStack();
                return true;
            } else if (itemId == R.id.navigation_scan) {
                return true; // Already here
            } else if (itemId == R.id.navigation_history) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HistoryFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_logout) {
                new AlertDialog.Builder(getContext())
                        .setTitle("退出登录")
                        .setMessage("您确定要退出登录吗?")
                        .setPositiveButton("确定", (dialog, which) -> {
                            if (getActivity() != null) {
                                // Clear SharedPreferences
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
                                sharedPreferences.edit().clear().apply();
                                // Navigate to LoginFragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new LoginFragment())
                                        .commit();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_scan);
    }
}
