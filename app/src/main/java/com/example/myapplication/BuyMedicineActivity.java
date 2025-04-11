package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class BuyMedicineActivity extends AppCompatActivity {
    private static final String MYDAWA_URL = "https://mydawa.com/";

    private final String[][] medicines = {
            {"Aspirin", "Pain relief and fever reduction", "Common pain reliever", "500mg tablets", "50"},
            {"Ibuprofen", "Anti-inflammatory", "Pain and inflammation", "400mg tablets", "50"},
            {"Acetaminophen", "Pain relief", "Fever and mild pain", "500mg tablets", "50"},
            {"Lisinopril", "Blood pressure medication", "Hypertension treatment", "10mg tablets", "50"},
            {"Atorvastatin", "Cholesterol medication", "Reduces cholesterol", "20mg tablets", "50"},
            {"Metformin", "Diabetes medication", "Blood sugar control", "850mg tablets", "50"},
            {"Amoxicillin", "Antibiotic", "Bacterial infections", "500mg capsules", "50"},
            {"Omeprazole", "Acid reducer", "Heartburn relief", "20mg capsules", "50"}
    };
    
    private final String[] medicineDetails = {
            "Pain Relief:\n- Used for mild to moderate pain\n- Reduces fever\n- Anti-inflammatory properties\n\n" +
            "Blood Pressure Control:\n- Helps lower blood pressure\n- Protects heart and kidneys\n- Once daily dosing\n\n" +
            "Cholesterol Management:\n- Lowers bad cholesterol (LDL)\n- Reduces heart disease risk\n- Evening dosing\n\n" +
            "Diabetes Management:\n- Controls blood sugar\n- Improves insulin sensitivity\n- Take with meals\n\n" +
            "Antibiotics:\n- Treats bacterial infections\n- Complete full course\n- Take as prescribed\n\n" +
            "Gastrointestinal:\n- Reduces stomach acid\n- Treats ulcers\n- Take before meals\n\n" +
            "Common Side Effects:\n- May cause drowsiness\n- Take with food if needed\n- Report severe reactions\n\n" +
            "Storage Instructions:\n- Keep at room temperature\n- Away from moisture\n- Out of reach of children"
    };

    private ListView medicineListView;
    private Button btnBack, btnGoToCart, btnVisitMydawa;
    private ArrayList<HashMap<String, String>> medicineList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine);

        initializeViews();
        setupButtons();
        populateMedicineList();
        setupListView();
    }

    private void initializeViews() {
        medicineListView = findViewById(R.id.listViewBM);
        btnBack = findViewById(R.id.buttonBMCBack);
        btnGoToCart = findViewById(R.id.buttonBMGoToCart);
        btnVisitMydawa = findViewById(R.id.buttonVisitMydawa);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BuyMedicineActivity.this, CartBuyMedicineActivity.class));
            }
        });

        btnVisitMydawa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MYDAWA_URL));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(BuyMedicineActivity.this, 
                        "Unable to open website. Please check your internet connection.", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateMedicineList() {
        medicineList = new ArrayList<>();
        for (String[] medicine : medicines) {
            HashMap<String, String> item = new HashMap<>();
            item.put("line1", medicine[0]); // Name
            item.put("line2", medicine[1]); // Category
            item.put("line3", medicine[2]); // Description
            item.put("line4", medicine[3]); // Dosage
            item.put("line5", "Price: KES " + medicine[4]); // Price
            medicineList.add(item);
        }
    }

    private void setupListView() {
        adapter = new SimpleAdapter(
            this,
            medicineList,
            R.layout.multi_lines,
            new String[]{"line1", "line2", "line3", "line4", "line5"},
            new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e}
        );
        
        medicineListView.setAdapter(adapter);

        medicineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < medicines.length) {
                    Intent intent = new Intent(BuyMedicineActivity.this, BuyMedicineDetailsActivity.class);
                    intent.putExtra("text1", medicines[position][0]); // Name
                    intent.putExtra("text2", medicineDetails[0]); // Details
                    intent.putExtra("text3", medicines[position][4]); // Price
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from cart or details
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}