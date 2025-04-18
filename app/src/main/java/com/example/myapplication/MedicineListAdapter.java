package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MedicineListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater;

    public MedicineListAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.multi_lines, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.medicine_image);
            holder.lineA = convertView.findViewById(R.id.line_a);
            holder.lineB = convertView.findViewById(R.id.line_b);
            holder.lineC = convertView.findViewById(R.id.line_c);
            holder.lineD = convertView.findViewById(R.id.line_d);
            holder.lineE = convertView.findViewById(R.id.line_e);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        HashMap<String, String> item = data.get(position);
        holder.lineA.setText(item.get("line1"));
        holder.lineB.setText(item.get("line2"));
        holder.lineC.setText(item.get("line3"));
        holder.lineD.setText(item.get("line4"));
        holder.lineE.setText(item.get("line5"));
        // Set image if available
        String imageResIdStr = item.get("imageResId");
        if (imageResIdStr != null) {
            try {
                int resId = Integer.parseInt(imageResIdStr);
                holder.imageView.setImageResource(resId);
                holder.imageView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                holder.imageView.setVisibility(View.GONE);
            }
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView lineA, lineB, lineC, lineD, lineE;
    }
}
