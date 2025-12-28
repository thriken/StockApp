package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse {

    private Data data;

    public Data getData() { return data; }

    public static class Data {
        private List<HistoryRecord> records;
        private Pagination pagination;
        private Filters filters;

        public List<HistoryRecord> getRecords() { return records; }
        public Pagination getPagination() { return pagination; }
        public Filters getFilters() { return filters; }
    }

    public static class Filters {
        @SerializedName("operation_types")
        private List<DropdownOptionsResponse.Option> operationTypes;

        private List<DropdownOptionsResponse.Option> bases;

        public List<DropdownOptionsResponse.Option> getOperationTypes() { return operationTypes; }
        public List<DropdownOptionsResponse.Option> getBases() { return bases; }
    }
}
