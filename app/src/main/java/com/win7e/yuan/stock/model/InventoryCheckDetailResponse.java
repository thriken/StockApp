package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InventoryCheckDetailResponse {
    private int code;
    private String msg;
    private Data data;

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public Data getData() { return data; }

    public static class Data {
        private Task task;
        private List<InventoryPackage> packages;

        public Task getTask() { return task; }
        public List<InventoryPackage> getPackages() { return packages; }
    }

    public static class Task {
        @SerializedName("total_packages")
        private int totalPackages;
        @SerializedName("checked_packages")
        private int checkedPackages;
        @SerializedName("difference_count")
        private int differenceCount;

        public int getTotalPackages() { return totalPackages; }
        public int getCheckedPackages() { return checkedPackages; }
        public int getDifferenceCount() { return differenceCount; }
    }
}
