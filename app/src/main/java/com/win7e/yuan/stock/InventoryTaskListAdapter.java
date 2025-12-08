package com.win7e.yuan.stock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.InventoryTask;

import java.util.List;

public class InventoryTaskListAdapter extends RecyclerView.Adapter<InventoryTaskListAdapter.ViewHolder> {

    private final List<InventoryTask> inventoryTasks;

    public InventoryTaskListAdapter(List<InventoryTask> inventoryTasks) {
        this.inventoryTasks = inventoryTasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryTask task = inventoryTasks.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskStatus.setText(task.getStatusText());
        holder.taskProgress.setText(String.format("%.2f%%", task.getCompletionRate()));
    }

    @Override
    public int getItemCount() {
        return inventoryTasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        TextView taskStatus;
        TextView taskProgress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.text_view_task_name);
            taskStatus = itemView.findViewById(R.id.text_view_task_status);
            taskProgress = itemView.findViewById(R.id.text_view_task_progress);
        }
    }
}
