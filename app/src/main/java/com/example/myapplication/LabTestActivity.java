package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LabTestActivity extends AppCompatActivity {

    public String[][] packages =
            {
                    {"Package 1 : Full Body Checkup","Blood Glucose Fasting, Complete Hemogram, and more","","","9999"},
                    {"Package 2 : Blood Glucose Fasting","Quick and accurate blood sugar test","","","2999"},
                    {"Package 3 : COVID-19 Antibody - IgG","Check your COVID-19 immunity status","","","8999"},
                    {"Package 4 : Thyroid Check","Complete thyroid profile test","","","4999"},
                    {"Package 5 : Immunity Check","Comprehensive immunity status check","","","6999"},
            };


    private final String[] package_details= {
            "Blood Glucose Fasting\n" +
                    "Complete Hemogram\n" +
                    "HbA1c\n" +
                    "Iron Studies \n" +
                    "Kidney Function Test\n" +
                    "LDH Lactate Dehydrogenase, Serum\n" +
                    "Lipid Profile\n" +
                    "Liver Function Test",
            "Blood Glucose Fasting",
            "COVID-19 Antibody - IgG",
            "Thyroid Profile-Total (T3, T4 & TSH Ultra-sensitive)",
            "Complete Hemogram\n" +
                    "CRP (C Reactive Protein) Quantitative, Serum\n" +
                    "Iron Studies \n" +
                    "Kidney Function Test\n" +
                    "Vitamin D Total-25 Hydroxy\n" +
                    "Liver Function Test\n" +
                    "Lipid Profile"
    };
    HashMap<String,String> item;
    ArrayList<HashMap<String, String>> list;
    Button btnGoToCart, btnBack;
    RecyclerView recyclerView;
    LabTestAdapter adapter;
    View featuredPackageView;
    TextView featuredName, featuredDetails, featuredPrice;
    ImageView featuredIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test);
        
        btnGoToCart = findViewById(R.id.buttonBMToCart);
        btnBack = findViewById(R.id.buttonBMCBack);
        recyclerView = findViewById(R.id.recyclerViewBM);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestActivity.this, HomeActivity.class));
                finish();
            }
        });

        list = new ArrayList<>();
        // Show all packages (including Full Body Checkup) in the main grid
        for(int i = 0; i < packages.length; i++){
            item = new HashMap<>();
            item.put("line1", packages[i][0]);
            item.put("line2", packages[i][1]);
            item.put("line5", "Ksh " + packages[i][4] + "/-");
            list.add(item);
        }

        adapter = new LabTestAdapter(this, list, position -> {
            // Use actual position directly
            int actualPosition = position;
            Intent it = new Intent(LabTestActivity.this, LabTestDetailsActivity.class);
            it.putExtra("text1", packages[actualPosition][0]);
            it.putExtra("text2", package_details[actualPosition]);
            it.putExtra("text3", packages[actualPosition][4]);
            startActivity(it);
        });

        recyclerView.setAdapter(adapter);

        btnGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LabTestActivity.this, CartLabActivity.class));
            }
        });
    }
}