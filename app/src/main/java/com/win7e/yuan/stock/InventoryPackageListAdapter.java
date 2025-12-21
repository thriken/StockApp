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
        holder.lineNumberTextView.setText(String.format("%d.", position + 1));
        holder.packageCodeTextView.setText("包号: " + pkg.getPackageCode());
        holder.rackNameTextView.setText("库位: " + pkg.getRackName());
        holder.piecesTextView.setText("片数: " + pkg.getPieces());
    }

    @Override
    public int getItemCount() {
        return packages != null ? packages.size() : 0;
    }

    public void updateData(List<InventoryPackage> newPackages) {
        packages.clear();
        if (newPackages != null) {
            packages.addAll(newPackages);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lineNumberTextView, packageCodeTextView, rackNameTextView, piecesTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            lineNumberTextView = itemView.findViewById(R.id.text_view_line_number);
            packageCodeTextView = itemView.findViewById(R.id.text_view_package_code);
            rackNameTextView = itemView.findViewById(R.id.text_view_rack_name);
            piecesTextView = itemView.findViewById(R.id.text_view_pieces);
        }
    }
}
