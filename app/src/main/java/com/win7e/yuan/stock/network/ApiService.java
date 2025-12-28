package com.win7e.yuan.stock.network;

import com.win7e.yuan.stock.model.AppInfoResponse;
import com.win7e.yuan.stock.model.BaseListResponse;
import com.win7e.yuan.stock.model.DropdownOptionsResponse;
import com.win7e.yuan.stock.model.HistoryResponse;
import com.win7e.yuan.stock.model.InventoryCheckDetailResponse;
import com.win7e.yuan.stock.model.InventoryCheckRequest;
import com.win7e.yuan.stock.model.InventoryTaskListResponse;
import com.win7e.yuan.stock.model.LoginRequest;
import com.win7e.yuan.stock.model.LoginResponse;
import com.win7e.yuan.stock.model.PackageResponse;
import com.win7e.yuan.stock.model.RawGlassDetailResponse;
import com.win7e.yuan.stock.model.RawGlassSearchResponse;
import com.win7e.yuan.stock.model.ScanRequest;
import com.win7e.yuan.stock.model.ScanResponse;
import com.win7e.yuan.stock.model.TargetInfoResponse;
import com.win7e.yuan.stock.model.TransferRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    @POST("auth.php")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("auth.php")
    Call<AppInfoResponse> getAppInfo(@Query("action") String action);

    @GET("scan.php")
    Call<ScanResponse> getPackageInfo(@Header("Authorization") String token, @Query("action") String action, @Query("package_code") String packageCode);

    @GET("scan.php")
    Call<BaseListResponse> getBases(@Header("Authorization") String token, @Query("action") String action);

    @GET("scan.php")
    Call<TargetInfoResponse> getTargetInfo(@Header("Authorization") String token, @Query("action") String action, @Query("target_rack_code") String rackCode, @Query("current_area_type") String currentAreaType);

    @POST("scan.php")
    Call<ScanResponse> executeTransaction(@Header("Authorization") String token, @Body ScanRequest scanRequest);

    @POST("scan.php")
    Call<ScanResponse> transferPackage(
        @Header("Authorization") String token,
        @Query("action") String action, // Should be "location_adjust"
        @Body TransferRequest request
    );

    @GET("inventory_check.php")
    Call<InventoryTaskListResponse> getInventoryTasks(@Header("Authorization") String token, @Query("action") String action);

    @GET("inventory_check.php")
    Call<InventoryCheckDetailResponse> getInventoryCheckDetail(@Header("Authorization") String token, @Query("action") String action, @Query("task_id") int taskId);

    @POST("inventory_check.php")
    Call<ScanResponse> submitInventoryCheck(@Header("Authorization") String token, @Body InventoryCheckRequest request);

    // Raw Glass APIs
    @GET("packages.php")
    Call<RawGlassSearchResponse> searchRawGlasses(
        @Header("Authorization") String token,
        @QueryMap Map<String, String> options
    );

    @GET("packages.php")
    Call<DropdownOptionsResponse> getDropdownOptions(
        @Header("Authorization") String token,
        @QueryMap Map<String, String> options
    );

    @GET("packages.php")
    Call<RawGlassDetailResponse> getRawGlassDetail(
        @Header("Authorization") String token,
        @Query("action") String action, // Should be "glass_type_summary"
        @Query("glass_type_id") int glassTypeId,
        @Query("base_all") Boolean baseAll
    );

    @GET("packages.php")
    Call<PackageResponse> getPackagesByGlassType(
        @Header("Authorization") String token,
        @Query("glass_type_id") int glassTypeId,
        @Query("page") int page,
        @Query("page_size") int pageSize,
        @Query("base_all") Boolean baseAll
    );

    // History API
    @GET("history.php")
    Call<HistoryResponse> getHistory(
        @Header("Authorization") String token,
        @QueryMap Map<String, String> options
    );
}
