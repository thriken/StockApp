package com.win7e.yuan.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.win7e.yuan.stock.model.InventoryTask;

import java.util.List;

public class InventoryTaskListAdapter extends RecyclerView.Adapter<InventoryTaskListAdapter.ViewHolder> {

    private final List<InventoryTask> inventoryTasks;
    private final Fragment fragment;

    public InventoryTaskListAdapter(List<InventoryTask> inventoryTasks, Fragment fragment) {
        this.inventoryTasks = inventoryTasks;
        this.fragment = fragment;
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
        holder.taskName.setText(String.format("#%d %s", task.getId(), task.getTaskName()));
        holder.taskStatus.setText(task.getStatusText());
        holder.progress.setText(String.format("%d / %d", task.getCheckedPackages(), task.getTotalPackages()));
        holder.difference.setText(String.valueOf(task.getDifferenceCount()));

        holder.itemView.setOnClickListener(v -> {
            // Create a new instance of the detail fragment
            InventoryCheckDetailFragment detailFragment = new InventoryCheckDetailFragment();

            // Create a bundle to pass the task ID
            Bundle bundle = new Bundle();
            bundle.putInt("taskId", task.getId());
            detailFragment.setArguments(bundle);

            // Use the FragmentManager to perform the transaction, consistent with the rest of the app
            fragment.getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Add to back stack to allow returning
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return inventoryTasks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskStatus, progress, difference;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.text_view_task_name);
            taskStatus = itemView.findViewById(R.id.text_view_task_status);
            progress = itemView.findViewById(R.id.text_view_progress);
            difference = itemView.findViewById(R.id.text_view_difference);
        }
    }
}
