package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.RawGlassDetail;
import java.util.List;

public class BaseDistributionAdapter extends RecyclerView.Adapter<BaseDistributionAdapter.ViewHolder> {

    private final List<RawGlassDetail.BaseDistribution> bases;

    public BaseDistributionAdapter(List<RawGlassDetail.BaseDistribution> bases) {
        this.bases = bases;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_base_distribution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RawGlassDetail.BaseDistribution base = bases.get(position);
        holder.bind(base);
    }

    @Override
    public int getItemCount() {
        return bases.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView baseName;
        RecyclerView rackRecyclerView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            baseName = itemView.findViewById(R.id.text_view_base_name);
            rackRecyclerView = itemView.findViewById(R.id.recycler_view_rack_distribution);
        }

        void bind(RawGlassDetail.BaseDistribution base) {
            baseName.setText(String.format("%s (共%d包)", base.getBaseName(), base.getTotalPackages()));

            rackRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            RackDistributionAdapter rackAdapter = new RackDistributionAdapter(base.getRacks());
            rackRecyclerView.setAdapter(rackAdapter);
        }
    }
}
