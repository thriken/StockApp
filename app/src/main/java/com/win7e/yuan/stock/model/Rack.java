package com.win7e.yuan.stock.model;

public class Rack {
    private int id;
    private int base_id;
    private String base_name;
    private String code;
    private String name;
    private String area_type;
    private String area_type_name;
    private int capacity;
    private String status;
    private String status_name;
    private int package_count;
    private int total_pieces;
    private String created_at;
    private String updated_at;

    // Getters
    public int getId() { return id; }
    public int getBaseId() { return base_id; }
    public String getBaseName() { return base_name; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getAreaType() { return area_type; }
    public String getAreaTypeName() { return area_type_name; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public String getStatusName() { return status_name; }
    public int getPackageCount() { return package_count; }
    public int getTotalPieces() { return total_pieces; }
    public String getCreatedAt() { return created_at; }
    public String getUpdatedAt() { return updated_at; }
}
