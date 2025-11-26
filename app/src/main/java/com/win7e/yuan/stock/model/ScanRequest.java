package com.win7e.yuan.stock.model;

public class ScanRequest {
    private String package_code;
    private String target_rack_code;
    private int quantity;
    private String transaction_type;
    private String scrap_reason;
    private String notes;
    private boolean all_use; // Added for the new checkbox feature

    public ScanRequest(String package_code, String target_rack_code, int quantity, String transaction_type, String notes, boolean all_use) {
        this.package_code = package_code;
        this.target_rack_code = target_rack_code;
        this.quantity = quantity;
        this.transaction_type = transaction_type;
        this.notes = notes;
        this.all_use = all_use;
    }

    public void setScrapReason(String scrap_reason) {
        this.scrap_reason = scrap_reason;
    }
}
