package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.RackGroup;
import java.util.List;

public class DetailedRackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_RACK = 1;

    private final List<Object> displayList;

    public DetailedRackAdapter(List<Object> displayList) {
        this.displayList = displayList;
    }

    @Override
    public int getItemViewType(int position) {
        if (displayList.get(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_RACK;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_base_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detailed_rack, parent, false);
            return new RackViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bind((String) displayList.get(position));
        } else {
            RackViewHolder rackHolder = (RackViewHolder) holder;
            rackHolder.bind((RackGroup) displayList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ViewHolder for the Base Name Header
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView baseNameHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            baseNameHeader = itemView.findViewById(R.id.text_view_base_name_header);
        }

        void bind(String baseName) {
            baseNameHeader.setText(baseName);
        }
    }

    // ViewHolder for the Rack Details
    static class RackViewHolder extends RecyclerView.ViewHolder {
        TextView rackNameHeader;
        RecyclerView specGroupRecyclerView;

        RackViewHolder(@NonNull View itemView) {
            super(itemView);
            rackNameHeader = itemView.findViewById(R.id.text_view_rack_name_header);
            specGroupRecyclerView = itemView.findViewById(R.id.recycler_view_spec_group);
        }

        void bind(RackGroup rackGroup) {
            rackNameHeader.setText("库位: " + rackGroup.getRackName());

            specGroupRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            SpecGroupAdapter specAdapter = new SpecGroupAdapter(rackGroup.getSpecGroups());
            specGroupRecyclerView.setAdapter(specAdapter);
        }
    }
}
