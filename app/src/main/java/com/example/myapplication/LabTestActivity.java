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

        // Setup featured package
        featuredPackageView = findViewById(R.id.featuredPackage);
        featuredName = featuredPackageView.findViewById(R.id.line_a);
        featuredDetails = featuredPackageView.findViewById(R.id.line_b);
        featuredPrice = featuredPackageView.findViewById(R.id.line_c);
        featuredIcon = featuredPackageView.findViewById(R.id.packageIcon);

        featuredName.setText(packages[0][0]);
        featuredDetails.setText(packages[0][1]);
        featuredPrice.setText("Ksh " + packages[0][4] + "/-");
        featuredIcon.setImageResource(R.drawable.bodycheckup);

        featuredPackageView.setOnClickListener(v -> {
            Intent it = new Intent(LabTestActivity.this, LabTestDetailsActivity.class);
            it.putExtra("text1", packages[0][0]);
            it.putExtra("text2", package_details[0]);
            it.putExtra("text3", packages[0][4]);
            startActivity(it);
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestActivity.this, HomeActivity.class));
                finish();
            }
        });

        list = new ArrayList<>();
        // Start from index 1 since index 0 is featured
        for(int i = 1; i < packages.length; i++){
            item = new HashMap<>();
            item.put("line1", packages[i][0]);
            item.put("line2", packages[i][1]);
            item.put("line5", "Ksh " + packages[i][4] + "/-");
            list.add(item);
        }

        adapter = new LabTestAdapter(this, list, position -> {
            // Add 1 to position since we skipped the featured package
            int actualPosition = position + 1;
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