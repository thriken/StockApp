package com.win7e.yuan.stock.network;

import com.win7e.yuan.stock.model.InventoryTaskListResponse;
import com.win7e.yuan.stock.model.InventoryTask;
import com.win7e.yuan.stock.model.LoginRequest;
import com.win7e.yuan.stock.model.LoginResponse;
import com.win7e.yuan.stock.model.ScanRequest;
import com.win7e.yuan.stock.model.ScanResponse;
import com.win7e.yuan.stock.model.TargetInfoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth.php")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("scan.php")
    Call<ScanResponse> getPackageInfo(@Header("Authorization") String token, @Query("action") String action, @Query("package_code") String packageCode);

    @GET("scan.php")
    Call<TargetInfoResponse> getTargetInfo(@Header("Authorization") String token, @Query("action") String action, @Query("target_rack_code") String rackCode, @Query("current_area_type") String currentAreaType);

    @POST("scan.php")
    Call<ScanResponse> executeTransaction(@Header("Authorization") String token, @Body ScanRequest scanRequest);

    @GET("inventory_check.php")
    Call<InventoryTaskListResponse> getInventoryTasks(@Header("Authorization") String token, @Query("action") String action);
}
