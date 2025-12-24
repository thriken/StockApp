package com.win7e.yuan.stock.model;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DropdownOptionsResponse {

    private Data data;

    public Data getData() { return data; }

    public static class Data {
        @SerializedName("available_options")
        private AvailableOptions availableOptions;

        // This field is now the same as in the fuzzy search response.
        @SerializedName("selection_options")
        private List<RawGlass> selectionOptions;

        public AvailableOptions getAvailableOptions() { return availableOptions; }
        public List<RawGlass> getSelectionOptions() { return selectionOptions; } // Changed from getGlassTypes
    }

    public static class AvailableOptions {
        private List<Option> thicknesses;
        private List<Option> colors;
        private List<Option> brands;

        public List<Option> getThicknesses() { return thicknesses; }
        public List<Option> getColors() { return colors; }
        public List<Option> getBrands() { return brands; }
    }

    public static class Option {
        private String value;
        private String label;

        public String getValue() { return value; }
        public String getLabel() { return label; }

        public void setLabel(String label) { this.label = label; }

        @NonNull
        @Override
        public String toString() {
            return label;
        }
    }
}
