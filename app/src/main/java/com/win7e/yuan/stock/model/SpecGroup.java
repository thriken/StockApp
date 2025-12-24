package com.win7e.yuan.stock.model;

public class SpecGroup {
    private final String specAndPieces;
    private final int packageCount;

    public SpecGroup(String specAndPieces, int packageCount) {
        this.specAndPieces = specAndPieces;
        this.packageCount = packageCount;
    }

    public String getSpecAndPieces() {
        return specAndPieces;
    }

    public int getPackageCount() {
        return packageCount;
    }
}
