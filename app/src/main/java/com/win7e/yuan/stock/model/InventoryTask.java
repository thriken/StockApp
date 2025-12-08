package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

public class InventoryTask {

    private int id;
    @SerializedName("task_name")
    private String taskName;
    @SerializedName("task_type")
    private String taskType;
    private String status;
    @SerializedName("total_packages")
    private int totalPackages;
    @SerializedName("checked_packages")
    private int checkedPackages;
    @SerializedName("difference_count")
    private int differenceCount;
    @SerializedName("completion_rate")
    private double completionRate;
    @SerializedName("status_text")
    private String statusText;
    @SerializedName("task_type_text")
    private String taskTypeText;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("started_at")
    private String startedAt;

    // Getters
    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getTaskType() { return taskType; }
    public String getStatus() { return status; }
    public int getTotalPackages() { return totalPackages; }
    public int getCheckedPackages() { return checkedPackages; }
    public int getDifferenceCount() { return differenceCount; }
    public double getCompletionRate() { return completionRate; }
    public String getStatusText() { return statusText; }
    public String getTaskTypeText() { return taskTypeText; }
    public String getCreatedAt() { return createdAt; }
    public String getStartedAt() { return startedAt; }
}
