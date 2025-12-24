package com.win7e.yuan.stock;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import com.google.android.material.tabs.TabLayout;
import com.win7e.yuan.stock.model.DropdownOptionsResponse;
import com.win7e.yuan.stock.model.RawGlass;
import com.win7e.yuan.stock.model.RawGlassSearchResponse;
import com.win7e.yuan.stock.network.ApiService;
import com.win7e.yuan.stock.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RawGlassSearchFragment extends Fragment implements RawGlassListAdapter.OnItemClickListener {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private RawGlassListAdapter adapter;
    private List<RawGlass> rawGlassList = new ArrayList<>();
    private CheckBox baseAllCheckBox;
    private TabLayout searchModeTabLayout;
    private LinearLayout fuzzySearchContainer, dropdownFilterContainer;
    private Spinner thicknessSpinner, colorSpinner, brandSpinner;

    private String lastSelectedThickness = null;
    private String lastSelectedColor = null;
    private String lastSelectedBrand = null;
    private int lastSelectedTabPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_raw_glass_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupToolbar(view);
        initializeViews(view);
        setupRecyclerView();
        setupSearchModeTabs();
        setupFuzzySearch();
        setupDropdownFilters();
        setupBaseAllCheckBoxListener();

        // Restore the last selected tab
        if (lastSelectedTabPosition != searchModeTabLayout.getSelectedTabPosition()) {
            searchModeTabLayout.selectTab(searchModeTabLayout.getTabAt(lastSelectedTabPosition));
        }
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.edit_text_search);
        recyclerView = view.findViewById(R.id.recycler_view_raw_glass);
        baseAllCheckBox = view.findViewById(R.id.checkbox_base_all);
        searchModeTabLayout = view.findViewById(R.id.tab_layout_search_mode);
        fuzzySearchContainer = view.findViewById(R.id.container_fuzzy_search);
        dropdownFilterContainer = view.findViewById(R.id.container_dropdown_filter);
        thicknessSpinner = view.findViewById(R.id.spinner_thickness);
        colorSpinner = view.findViewById(R.id.spinner_color);
        brandSpinner = view.findViewById(R.id.spinner_brand);
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RawGlassListAdapter(rawGlassList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchModeTabs() {
        searchModeTabLayout.addTab(searchModeTabLayout.newTab().setText("模糊搜索"));
        searchModeTabLayout.addTab(searchModeTabLayout.newTab().setText("三联动筛选"));
        searchModeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                lastSelectedTabPosition = tab.getPosition(); // Save the selected tab position
                clearResults();
                if (tab.getPosition() == 0) {
                    fuzzySearchContainer.setVisibility(View.VISIBLE);
                    dropdownFilterContainer.setVisibility(View.GONE);
                } else {
                    fuzzySearchContainer.setVisibility(View.GONE);
                    dropdownFilterContainer.setVisibility(View.VISIBLE);
                    resetAndFetchDropdownOptions();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void setupFuzzySearch() {
        Button fuzzySearchButton = requireView().findViewById(R.id.button_fuzzy_search);
        fuzzySearchButton.setOnClickListener(v -> performFuzzySearchAction());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performFuzzySearchAction();
                return true;
            }
            return false;
        });
    }

    private void performFuzzySearchAction() {
        String keyword = searchEditText.getText().toString().trim();
        if (!keyword.isEmpty()) {
            performFuzzySearch(keyword);
        }
    }

    private void setupDropdownFilters() {
        thicknessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleSpinnerSelection(thicknessSpinner, lastSelectedThickness, (newValue) -> lastSelectedThickness = newValue);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleSpinnerSelection(colorSpinner, lastSelectedColor, (newValue) -> lastSelectedColor = newValue);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleSpinnerSelection(brandSpinner, lastSelectedBrand, (newValue) -> lastSelectedBrand = newValue);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        Button filterSearchButton = requireView().findViewById(R.id.button_filter_search);
        filterSearchButton.setOnClickListener(v -> fetchDropdownOptions(true));
    }

    private void setupBaseAllCheckBoxListener(){
        baseAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (searchModeTabLayout.getSelectedTabPosition() == 1) {
                resetAndFetchDropdownOptions();
            }
        });
    }

    private void handleSpinnerSelection(Spinner spinner, String lastValue, java.util.function.Consumer<String> valueUpdater) {
        String selectedValue = null;
        if (spinner.getSelectedItemPosition() > 0) {
            DropdownOptionsResponse.Option selected = (DropdownOptionsResponse.Option) spinner.getSelectedItem();
            selectedValue = selected.getValue();
        }

        if (!Objects.equals(lastValue, selectedValue)) {
            valueUpdater.accept(selectedValue);
            fetchDropdownOptions(false);
        }
    }
    
    private void resetAndFetchDropdownOptions(){
        lastSelectedThickness = null;
        lastSelectedColor = null;
        lastSelectedBrand = null;
        fetchDropdownOptions(false);
    }

    private void performFuzzySearch(String keyword) {
        Map<String, String> options = new HashMap<>();
        options.put("action", "fuzzy_search");
        options.put("keyword", keyword);
        if (baseAllCheckBox.isChecked()) {
            options.put("base_all", "true");
        }

        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.searchRawGlasses(token, options).enqueue(new Callback<RawGlassSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<RawGlassSearchResponse> call, @NonNull Response<RawGlassSearchResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    updateResults(response.body().getData().getSelectionOptions());
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "搜索失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RawGlassSearchResponse> call, @NonNull Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchDropdownOptions(boolean isFinalSearch) {
        Map<String, String> options = new HashMap<>();
        options.put("action", "get_dropdown_options");

        if (lastSelectedThickness != null) options.put("thickness", lastSelectedThickness);
        if (lastSelectedColor != null) options.put("color", lastSelectedColor);
        if (lastSelectedBrand != null) options.put("brand", lastSelectedBrand);

        if (isFinalSearch) {
            options.put("submit", "true");
        }

        if(baseAllCheckBox.isChecked()){
            options.put("base_all", "true");
        }

        ApiService apiService = RetrofitClient.getApiService(requireContext());
        String token = "Bearer " + requireContext().getSharedPreferences("stock_prefs", Context.MODE_PRIVATE).getString("token", "");

        apiService.getDropdownOptions(token, options).enqueue(new Callback<DropdownOptionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<DropdownOptionsResponse> call, @NonNull Response<DropdownOptionsResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    DropdownOptionsResponse.Data data = response.body().getData();
                    if (isFinalSearch) {
                        updateResults(data.getSelectionOptions());
                    } else {
                        updateSpinners(data.getAvailableOptions());
                    }
                }
            }
            @Override public void onFailure(@NonNull Call<DropdownOptionsResponse> call, @NonNull Throwable t) { }
        });
    }

    private void updateSpinners(DropdownOptionsResponse.AvailableOptions availableOptions) {
        updateSpinner(thicknessSpinner, availableOptions.getThicknesses(), "选择厚度", lastSelectedThickness);
        updateSpinner(colorSpinner, availableOptions.getColors(), "选择颜色", lastSelectedColor);
        updateSpinner(brandSpinner, availableOptions.getBrands(), "选择品牌", lastSelectedBrand);
    }

    private void updateSpinner(Spinner spinner, List<DropdownOptionsResponse.Option> options, String hint, String currentValue) {
        int selectionIndex = 0;
        List<DropdownOptionsResponse.Option> newOptions = new ArrayList<>();
        newOptions.add(createHintOption(hint));
        if (options != null) {
            newOptions.addAll(options);
        }

        ArrayAdapter<DropdownOptionsResponse.Option> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, newOptions);
        spinner.setAdapter(adapter);

        if (currentValue != null) {
            for (int i = 1; i < newOptions.size(); i++) { // Start from 1 to skip hint
                if (currentValue.equals(newOptions.get(i).getValue())) {
                    selectionIndex = i;
                    break;
                }
            }
        }
        spinner.setSelection(selectionIndex);
    }

    private DropdownOptionsResponse.Option createHintOption(String hint) {
        DropdownOptionsResponse.Option option = new DropdownOptionsResponse.Option();
        option.setLabel(hint);
        return option;
    }

    private void clearResults(){
        rawGlassList.clear();
        adapter.notifyDataSetChanged();
    }

    private void updateResults(List<RawGlass> results) {
        rawGlassList.clear();
        if (results != null) {
            rawGlassList.addAll(results);
        }
        adapter.notifyDataSetChanged();
        if (rawGlassList.isEmpty()) {
            Toast.makeText(getContext(), "未找到匹配的原片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(RawGlass rawGlass) {
        Bundle args = new Bundle();
        args.putInt("glassTypeId", rawGlass.getId());
        args.putBoolean("baseAll", baseAllCheckBox.isChecked());
        NavHostFragment.findNavController(this).navigate(R.id.action_rawGlassSearchFragment_to_rawGlassDetailFragment, args);
    }
}
