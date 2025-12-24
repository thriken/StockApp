package com.win7e.yuan.stock.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Represents the full response for a fuzzy_search API call for raw glasses.
 */
public class RawGlassSearchResponse {

    private int code;
    private String message;
    private long timestamp;
    private Data data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("selection_options")
        private List<RawGlass> selectionOptions;

        @SerializedName("pagination")
        private Pagination pagination;

        public List<RawGlass> getSelectionOptions() { return selectionOptions; }
        public Pagination getPagination() { return pagination; }
    }
}
