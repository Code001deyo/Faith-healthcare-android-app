package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {

    private ListView lst;
    private SimpleAdapter sa;
    private ArrayList<HashMap<String, String>> list;
    private HashMap<String, String> item;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        initializeViews();
        initializeButton();
        displayOrders();
    }

    private void initializeViews() {
        lst = findViewById(R.id.listViewBM);
    }

    private void initializeButton() {
        btn = findViewById(R.id.buttonBMCBack);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OrderDetailsActivity.this, HomeActivity.class));
            }
        });
    }

    private void displayOrders() {
        Database db = new Database(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        ArrayList dbData = db.getOrderData(username);
        list = new ArrayList<>();

        for (int i = 0; i < dbData.size(); i++) {
    String arrData = dbData.get(i).toString();
    String[] strData = arrData.split(java.util.regex.Pattern.quote("$"));
    if (strData.length >= 8) {
        item = new HashMap<>();
        item.put("line_a", strData[0]);
        item.put("line_b", strData[1]);
        if (strData[7].compareTo("medicine") == 0) {
            item.put("line_d", "Del:" + strData[4]);
            // Medicine image (use lowercased, underscores, no extension)
            String medName = strData[0].toLowerCase().replaceAll("[^a-z0-9]", "_");
            // Try to match the medicine image from drawable, fallback to a default if not found
            int resId = getResources().getIdentifier(medName, "drawable", getPackageName());
            if (resId == 0) medName = "aspirin"; // fallback
            item.put("image", medName);
        } else {
            item.put("line_d", "Del:" + strData[4] + " " + strData[5]);
            // Doctor image by gender
            String docName = strData[0].toLowerCase();
            if (docName.contains("sarah") || docName.contains("emily") || docName.contains("rachel") || docName.contains("lisa") || docName.contains("maria") || docName.contains("patricia") || docName.contains("elizabeth")) {
                item.put("image", "physician_female");
            } else {
                item.put("image", "physician_male");
            }
        }
        item.put("line_c", "Rs " + strData[6]);
        item.put("line_e", strData[7]);
        list.add(item);
    }
}

        sa = new SimpleAdapter(
    this,
    list,
    R.layout.multi_lines,
    new String[] {"line_a", "line_b", "line_c", "line_d", "line_e", "image"},
    new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, R.id.medicine_image}
);
sa.setViewBinder(new SimpleAdapter.ViewBinder() {
    @Override
    public boolean setViewValue(View view, Object data, String textRepresentation) {
        if (view.getId() == R.id.medicine_image && data instanceof String) {
            String imgName = (String) data;
            int resId = getResources().getIdentifier(imgName, "drawable", getPackageName());
            if (resId == 0) resId = getResources().getIdentifier("aspirin", "drawable", getPackageName());
            ((android.widget.ImageView) view).setImageResource(resId);
            return true;
        }
        return false;
    }
});
lst.setAdapter(sa);
    }
}