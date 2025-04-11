package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;

public class LabTestAdapter extends RecyclerView.Adapter<LabTestAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> list;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public LabTestAdapter(Context context, ArrayList<HashMap<String, String>> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lab_test_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> item = list.get(position);
        holder.packageName.setText(item.get("line1"));
        holder.packageDetails.setText(item.get("line2"));
        holder.packagePrice.setText(item.get("line5"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView packageName;
        TextView packageDetails;
        TextView packagePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.line_a);
            packageDetails = itemView.findViewById(R.id.line_b);
            packagePrice = itemView.findViewById(R.id.line_c);
        }
    }
}
