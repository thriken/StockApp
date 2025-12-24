package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.SpecGroup;
import java.util.List;

public class SpecGroupAdapter extends RecyclerView.Adapter<SpecGroupAdapter.ViewHolder> {

    private final List<SpecGroup> specGroups;

    public SpecGroupAdapter(List<SpecGroup> specGroups) {
        this.specGroups = specGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spec_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecGroup group = specGroups.get(position);
        holder.specAndPieces.setText(group.getSpecAndPieces());
        holder.packageCount.setText(String.format("%d包", group.getPackageCount()));
    }

    @Override
    public int getItemCount() {
        return specGroups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView specAndPieces, packageCount;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            specAndPieces = itemView.findViewById(R.id.text_view_spec_and_pieces);
            packageCount = itemView.findViewById(R.id.text_view_package_count);
        }
    }
}
