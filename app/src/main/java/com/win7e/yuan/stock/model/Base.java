package com.win7e.yuan.stock.model;

import androidx.annotation.NonNull;

public class Base {
    private int id;
    private String name;

    public int getId() { return id; }
    public String getName() { return name; }

    @NonNull
    @Override
    public String toString() {
        // This is crucial for the ArrayAdapter to display the name in the Spinner.
        return name;
    }
}
