package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the pagination object returned by the API.
 */
public class Pagination {

    private int page;

    @SerializedName("page_size")
    private int pageSize;

    private int total;

    @SerializedName("total_pages")
    private int totalPages;

    // Getters
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotal() { return total; }
    public int getTotalPages() { return totalPages; }
}
