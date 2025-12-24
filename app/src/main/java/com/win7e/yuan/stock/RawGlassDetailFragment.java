package com.win7e.yuan.stock;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.Package;
import com.win7e.yuan.stock.model.PackageResponse;
import com.win7e.yuan.stock.model.RackGroup;
import com.win7e.yuan.stock.model.RawGlassDetail;
import com.win7e.yuan.stock.model.RawGlassDetailResponse;
import com.win7e.yuan.stock.model.SpecGroup;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RawGlassDetailFragment extends Fragment {

    private int glassTypeId;
    private boolean baseAll;

    private TextView glassName, glassAttributes, summaryPackages, summaryPieces, summaryArea, summaryRacks;
    private RecyclerView detailedDistributionRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            glassTypeId = getArguments().getInt("glassTypeId", -1);
            baseAll = getArguments().getBoolean("baseAll", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_raw_glass_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar(view);
        initializeViews(view);

        if (glassTypeId != -1) {
            fetchRawGlassDetails();
        }
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void initializeViews(View view) {
        glassName = view.findViewById(R.id.text_view_glass_name);
        glassAttributes = view.findViewById(R.id.text_view_glass_attributes);
        summaryPackages = view.findViewById(R.id.text_view_summary_packages);
        summaryPieces = view.findViewById(R.id.text_view_summary_pieces);
        summaryArea = view.findViewById(R.id.text_view_summary_area);
        summaryRacks = view.findViewById(R.id.text_view_summary_racks);
        detailedDistributionRecyclerView = view.findViewById(R.id.recycler_view_detailed_distribution);
        detailedDistributionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void fetchRawGlassDetails() {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.getRawGlassDetail(token, "glass_type_summary", glassTypeId, baseAll).enqueue(new Callback<RawGlassDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<RawGlassDetailResponse> call, @NonNull Response<RawGlassDetailResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    updateSummaryUI(response.body().getData());
                    fetchAllPackagesForType(glassTypeId);
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "获取详情失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RawGlassDetailResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchAllPackagesForType(int glassTypeId) {
        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");
        List<Package> allPackages = new ArrayList<>();
        
        fetchPackagePage(apiService, token, glassTypeId, 1, allPackages);
    }

    private void fetchPackagePage(ApiService apiService, String token, int glassTypeId, int page, List<Package> allPackages) {
        apiService.getPackagesByGlassType(token, glassTypeId, page, 100, baseAll).enqueue(new Callback<PackageResponse>() {
            @Override
            public void onResponse(@NonNull Call<PackageResponse> call, @NonNull Response<PackageResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    PackageResponse.Data data = response.body().getData();
                    allPackages.addAll(data.getPackages());

                    if (data.getPagination() != null && page < data.getPagination().getTotalPages()) {
                        fetchPackagePage(apiService, token, glassTypeId, page + 1, allPackages);
                    } else {
                        processAndDisplayDetailedDistribution(allPackages);
                        calculateAndDisplayTotalArea(allPackages);
                    }
                } else if(isAdded()){
                    Toast.makeText(getContext(), "获取包列表失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PackageResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processAndDisplayDetailedDistribution(List<Package> packages) {
        Map<String, Map<String, Map<String, Integer>>> groupedByBase = packages.stream()
                .filter(p -> p.getRackInfo() != null && p.getRackInfo().getBaseName() != null && p.getRackInfo().getName() != null && p.getDimensions() != null && p.getQuantity() != null)
                .collect(Collectors.groupingBy(p -> p.getRackInfo().getBaseName(), LinkedHashMap::new, // Preserve order
                        Collectors.groupingBy(p -> p.getRackInfo().getName(), LinkedHashMap::new,
                                Collectors.groupingBy(p -> String.format(Locale.US, "%.0fx%.0f = %d片", p.getDimensions().getWidth(), p.getDimensions().getHeight(), p.getQuantity().getPieces()),
                                        Collectors.summingInt(p -> 1)))));

        List<Object> displayList = new ArrayList<>();
        for (Map.Entry<String, Map<String, Map<String, Integer>>> baseEntry : groupedByBase.entrySet()) {
            displayList.add(baseEntry.getKey());

            for (Map.Entry<String, Map<String, Integer>> rackEntry : baseEntry.getValue().entrySet()) {
                List<SpecGroup> specGroups = rackEntry.getValue().entrySet().stream()
                        .map(specEntry -> new SpecGroup(specEntry.getKey(), specEntry.getValue()))
                        .collect(Collectors.toList());
                displayList.add(new RackGroup(rackEntry.getKey(), specGroups));
            }
        }

        DetailedRackAdapter adapter = new DetailedRackAdapter(displayList);
        detailedDistributionRecyclerView.setAdapter(adapter);
    }

    private void calculateAndDisplayTotalArea(List<Package> packages) {
        double totalArea = 0.0;
        for (Package pkg : packages) {
            if (pkg.getDimensions() != null && pkg.getQuantity() != null) {
                totalArea += (pkg.getDimensions().getWidth() * 0.001) * (pkg.getDimensions().getHeight() * 0.001) * pkg.getQuantity().getPieces();
            }
        }
        summaryArea.setText(String.format(Locale.US, "总面积: %.2f m²", totalArea));
    }

    private void updateSummaryUI(RawGlassDetail detail) {
        RawGlassDetail.GlassTypeInfo info = detail.getGlassType();
        if (info == null) return;

        glassName.setText("原片名称: " + info.getName());

        if (info.getAttributes() != null) {
            RawGlassDetail.Attributes attrs = info.getAttributes();
            String attrsText = String.format(Locale.US, "品牌: %s | 厚度: %.1fmm | 颜色: %s",
                    attrs.getBrand(), attrs.getThickness(), attrs.getColor());
            glassAttributes.setText(attrsText);
        }

        if (detail.getInventorySummary() != null) {
            RawGlassDetail.InventorySummary summary = detail.getInventorySummary();
            summaryPackages.setText("总包数: " + summary.getTotalPackages());
            summaryPieces.setText("总片数: " + summary.getTotalPieces());
            summaryRacks.setText("占用库位数: " + summary.getTotalRacksUsed());
        }
    }
}
