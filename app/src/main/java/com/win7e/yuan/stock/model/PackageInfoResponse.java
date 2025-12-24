package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PackageInfoResponse {

    private int code;
    private Data data;

    public int getCode() { return code; }
    public Data getData() { return data; }

    public static class Data {
        private List<PackageInfo> packages;
        private Pagination pagination;

        public List<PackageInfo> getPackages() { return packages; }
        public Pagination getPagination() { return pagination; }
    }
}
