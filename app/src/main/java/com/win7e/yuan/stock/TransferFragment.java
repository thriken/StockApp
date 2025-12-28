package com.win7e.yuan.stock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.win7e.yuan.stock.model.Base;
import com.win7e.yuan.stock.model.BaseListResponse;
import com.win7e.yuan.stock.model.ScanResponse;
import com.win7e.yuan.stock.model.TransferRequest;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferFragment extends Fragment {

    private EditText packageCodeEditText;
    private LinearLayout packageInfoLayout;
    private TextView packageInfoTextView, specificationTextView, piecesTextView, entryDateTextView, currentBaseTextView;
    private Spinner targetBaseSpinner;
    private Button submitButton;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private ScanResponse.Data currentPackageInfo;
    private String currentUserBaseId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupToolbar(view);

        SharedPreferences prefs = requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE);
        currentUserBaseId = prefs.getString("base_id", "");

        setupPackageCodeWatcher();
        fetchBases();

        submitButton.setOnClickListener(v -> performTransfer());
    }

    private void initializeViews(View view) {
        packageCodeEditText = view.findViewById(R.id.edit_text_transfer_package_code);
        packageInfoLayout = view.findViewById(R.id.layout_transfer_package_info);
        packageInfoTextView = view.findViewById(R.id.text_view_transfer_package_info);
        specificationTextView = view.findViewById(R.id.text_view_transfer_specification);
        piecesTextView = view.findViewById(R.id.text_view_transfer_pieces);
        entryDateTextView = view.findViewById(R.id.text_view_transfer_entry_date);
        currentBaseTextView = view.findViewById(R.id.text_view_transfer_current_base);
        targetBaseSpinner = view.findViewById(R.id.spinner_target_base);
        submitButton = view.findViewById(R.id.button_submit_transfer);

        packageCodeEditText.requestFocus();
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void setupPackageCodeWatcher() {
        searchRunnable = () -> fetchPackageInfo(packageCodeEditText.getText().toString());
        packageCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                packageInfoLayout.setVisibility(View.GONE);
                currentPackageInfo = null;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    handler.postDelayed(searchRunnable, 1000);
                }
            }
        });
    }

    private void fetchPackageInfo(String packageCode) {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.getPackageInfo(token, "get_package_info", packageCode).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScanResponse> call, @NonNull Response<ScanResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    currentPackageInfo = response.body().getData();
                    if (currentPackageInfo != null && currentPackageInfo.getPackageCode() != null) {
                        packageInfoTextView.setText(String.format("包号: %s (%s)", currentPackageInfo.getPackageCode(), currentPackageInfo.getGlassName()));
                        specificationTextView.setText("规格: " + currentPackageInfo.getSpecification());
                        piecesTextView.setText(String.format("数量: %d片", currentPackageInfo.getPieces()));
                        entryDateTextView.setText("入库日期: " + currentPackageInfo.getEntryDate());
                        currentBaseTextView.setText("当前基地: " + currentPackageInfo.getBaseName());
                        packageInfoLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "未找到包信息", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "查询包信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScanResponse> call, @NonNull Throwable t) {
                if (isAdded()) Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchBases() {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.getBases(token, "get_bases").enqueue(new Callback<BaseListResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseListResponse> call, @NonNull Response<BaseListResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<Base> allBases = response.body().getData();
                    if (allBases != null) {
                        List<Base> targetBases = allBases.stream()
                                .filter(base -> !String.valueOf(base.getId()).equals(currentUserBaseId))
                                .collect(Collectors.toList());

                        ArrayAdapter<Base> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, targetBases);
                        targetBaseSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "未能获取基地列表", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "获取基地列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseListResponse> call, @NonNull Throwable t) {
                if(isAdded()) Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performTransfer() {
        if (currentPackageInfo == null) {
            Toast.makeText(getContext(), "请先输入有效的包号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (targetBaseSpinner.getSelectedItem() == null) {
            Toast.makeText(getContext(), "请选择目标基地", Toast.LENGTH_SHORT).show();
            return;
        }

        Base selectedBase = (Base) targetBaseSpinner.getSelectedItem();
        int targetBaseId = selectedBase.getId();

        TransferRequest request = new TransferRequest(currentPackageInfo.getPackageCode(), targetBaseId);
        
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.transferPackage(token, "location_adjust", request).enqueue(new Callback<ScanResponse>() {
            @Override
            public void onResponse(@NonNull Call<ScanResponse> call, @NonNull Response<ScanResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMsg(), Toast.LENGTH_LONG).show();
                    if (response.body().getCode() == 200) {
                        resetForm();
                    }
                } else if(isAdded()){
                    Toast.makeText(getContext(), "转移失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScanResponse> call, @NonNull Throwable t) {
                if(isAdded()) Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetForm() {
        packageCodeEditText.setText("");
        packageInfoLayout.setVisibility(View.GONE);
        if (targetBaseSpinner.getAdapter() != null && targetBaseSpinner.getAdapter().getCount() > 0) {
            targetBaseSpinner.setSelection(0);
        }
        packageCodeEditText.requestFocus();
        currentPackageInfo = null;
    }
}
