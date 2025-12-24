package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.RawGlassDetail;
import java.util.List;

public class RackDistributionAdapter extends RecyclerView.Adapter<RackDistributionAdapter.ViewHolder> {

    private final List<RawGlassDetail.RackDistribution> racks;

    public RackDistributionAdapter(List<RawGlassDetail.RackDistribution> racks) {
        this.racks = racks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rack_distribution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RawGlassDetail.RackDistribution rack = racks.get(position);
        holder.rackName.setText(rack.getRackName());
        holder.packageCount.setText(String.format("%d包", rack.getPackageCount()));
    }

    @Override
    public int getItemCount() {
        return racks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rackName, packageCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rackName = itemView.findViewById(R.id.text_view_rack_name);
            packageCount = itemView.findViewById(R.id.text_view_package_count);
        }
    }
}
