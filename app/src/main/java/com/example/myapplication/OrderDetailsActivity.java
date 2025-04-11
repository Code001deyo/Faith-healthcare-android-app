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
                } else {
                    item.put("line_d", "Del:" + strData[4] + " " + strData[5]);
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
            new String[] {"line_a", "line_b", "line_c", "line_d", "line_e"},
            new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e}
        );

        lst.setAdapter(sa);
    }
}