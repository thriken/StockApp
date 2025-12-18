package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.InventoryPackage;

import java.util.List;

public class InventoryPackageListAdapter extends RecyclerView.Adapter<InventoryPackageListAdapter.ViewHolder> {

    private final List<InventoryPackage> packages;

    public InventoryPackageListAdapter(List<InventoryPackage> packages) {
        this.packages = packages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryPackage pkg = packages.get(position);
        holder.packageCodeTextView.setText(pkg.getPackageCode());
        holder.rackCodeTextView.setText(pkg.getRackCode());
        holder.quantityTextView.setText(String.valueOf(pkg.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    public void updateData(List<InventoryPackage> newPackages) {
        packages.clear();
        packages.addAll(newPackages);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView packageCodeTextView, rackCodeTextView, quantityTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            packageCodeTextView = itemView.findViewById(R.id.text_view_package_code);
            rackCodeTextView = itemView.findViewById(R.id.text_view_rack_code);
            quantityTextView = itemView.findViewById(R.id.text_view_quantity);
        }
    }
}
