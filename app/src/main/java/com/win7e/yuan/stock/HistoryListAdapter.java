package com.win7e.yuan.stock;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.win7e.yuan.stock.model.HistoryRecord;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder> {

    private List<HistoryRecord> records;
    private Map<String, String> operationTypeNames;

    public HistoryListAdapter(List<HistoryRecord> records, Map<String, String> operationTypeNames) {
        this.records = records;
        this.operationTypeNames = operationTypeNames;
    }

    public void updateOperationTypeNames(Map<String, String> newNames) {
        this.operationTypeNames = newNames;
        // No need to notify change here, as this map is used during onBindViewHolder,
        // which will be called when the list itself is updated.
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryRecord record = records.get(position);
        holder.bind(record, operationTypeNames);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView operationType, datetime, packageInfo, rackFlow, quantityChange, operator, base;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            operationType = itemView.findViewById(R.id.text_view_operation_type);
            datetime = itemView.findViewById(R.id.text_view_datetime);
            packageInfo = itemView.findViewById(R.id.text_view_package_info);
            rackFlow = itemView.findViewById(R.id.text_view_rack_flow);
            quantityChange = itemView.findViewById(R.id.text_view_quantity_change);
            operator = itemView.findViewById(R.id.text_view_operator);
            base = itemView.findViewById(R.id.text_view_base);
        }

        void bind(HistoryRecord record, Map<String, String> operationTypeNames) {
            Context context = itemView.getContext();
            String typeCode = record.getOperationType();

            // Set Text
            String typeName = operationTypeNames.getOrDefault(typeCode, typeCode);
            operationType.setText(typeName);
            datetime.setText(String.format("%s %s", record.getOperationDate(), record.getOperationTime()));

            // Set Colors based on operation type
            int backgroundColor, textColor;
            switch (typeCode) {
                case "purchase_in":
                case "check_in":
                    backgroundColor = R.color.history_in_bg;
                    textColor = R.color.history_in_text;
                    break;
                case "usage_out":
                case "partial_usage":
                case "check_out":
                    backgroundColor = R.color.history_out_bg;
                    textColor = R.color.history_out_text;
                    break;
                case "return_in":
                    backgroundColor = R.color.history_return_bg;
                    textColor = R.color.history_return_text;
                    break;
                case "scrap":
                    backgroundColor = R.color.history_scrap_bg;
                    textColor = R.color.history_scrap_text;
                    break;
                case "location_adjust":
                    backgroundColor = R.color.history_adjust_bg;
                    textColor = R.color.history_adjust_text;
                    break;
                default:
                    backgroundColor = R.color.white; // Default color
                    textColor = R.color.black;
                    break;
            }
            GradientDrawable background = (GradientDrawable) operationType.getBackground();
            background.setColor(ContextCompat.getColor(context, backgroundColor));
            operationType.setTextColor(ContextCompat.getColor(context, textColor));

            // --- Rest of the binding ---
            packageInfo.setText(String.format("包号: %s (%s)", record.getPackageCode(), record.getGlassName()));
            String from = record.getFromRackCode() != null ? record.getFromRackCode() : "";
            String to = record.getToRackCode() != null ? record.getToRackCode() : "";
            rackFlow.setText(String.format("库位: %s -> %s", from, to));
            int opQty = record.getOperationQuantity();
            String opSign = (opQty >= 0 && !typeCode.contains("out") && !typeCode.contains("scrap")) ? "+" : "";
            if(typeCode.contains("check_out")) opSign = "-";

            quantityChange.setText(String.format(Locale.US, "数量: %d -> %d (操作: %s%d)",
                    record.getBeforeQuantity(), record.getAfterQuantity(), opSign, opQty));

            operator.setText("操作员: " + record.getOperatorName());
            base.setText("基地: " + record.getBaseName());
        }
    }
}
