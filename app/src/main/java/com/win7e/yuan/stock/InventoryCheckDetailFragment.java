package com.win7e.yuan.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InventoryCheckDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_check_detail, container, false);
        TextView taskIdTextView = view.findViewById(R.id.text_view_task_id);

        if (getArguments() != null) {
            int taskId = getArguments().getInt("taskId", -1);
            if (taskId != -1) {
                taskIdTextView.setText("Task ID: " + taskId);
            }
        }

        return view;
    }
}
