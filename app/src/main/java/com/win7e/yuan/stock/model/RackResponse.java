package com.win7e.yuan.stock.model;

import java.util.List;

public class RackResponse {
    private int code;
    private String message;
    private long timestamp;
    private RackData data;

    // Getters
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public RackData getData() { return data; }

    public static class RackData {
        private List<Rack> racks;
        private Pagination pagination;

        // Getters
        public List<Rack> getRacks() { return racks; }
        public Pagination getPagination() { return pagination; }
    }

    public static class Pagination {
        private int page;
        private int page_size;
        private int total;
        private int total_pages;

        // Getters
        public int getPage() { return page; }
        public int getPageSize() { return page_size; }
        public int getTotal() { return total; }
        public int getTotalPages() { return total_pages; }
    }
}
