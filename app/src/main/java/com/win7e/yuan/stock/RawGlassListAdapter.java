package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.RawGlass;
import java.util.List;
import java.util.Locale;

public class RawGlassListAdapter extends RecyclerView.Adapter<RawGlassListAdapter.ViewHolder> {

    private final List<RawGlass> rawGlasses;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RawGlass rawGlass);
    }

    public RawGlassListAdapter(List<RawGlass> rawGlasses, OnItemClickListener listener) {
        this.rawGlasses = rawGlasses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_raw_glass, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RawGlass rawGlass = rawGlasses.get(position);
        holder.bind(rawGlass, listener);
    }

    @Override
    public int getItemCount() {
        return rawGlasses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayName, attributes, inventory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.text_view_display_name);
            attributes = itemView.findViewById(R.id.text_view_attributes);
            inventory = itemView.findViewById(R.id.text_view_inventory);
        }

        public void bind(final RawGlass rawGlass, final OnItemClickListener listener) {
            displayName.setText(rawGlass.getDisplayName());

            // Attributes are only available in fuzzy search, hide the view if they don't exist.
            if (rawGlass.getBrand() != null && rawGlass.getColor() != null) {
                String attrs = String.format(Locale.US, "品牌: %s | 厚度: %.1fmm | 颜色: %s",
                    rawGlass.getBrand(), rawGlass.getThickness(), rawGlass.getColor());
                attributes.setText(attrs);
                attributes.setVisibility(View.VISIBLE);
            } else {
                attributes.setVisibility(View.GONE);
            }

            // totalPackages is only available in fuzzy search, hide the view if it's 0 or less.
            if (rawGlass.getTotalPackages() > 0) {
                inventory.setText(String.format(Locale.US, "库存: %d包", rawGlass.getTotalPackages()));
                inventory.setVisibility(View.VISIBLE);
            } else {
                inventory.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(rawGlass));
        }
    }
}
