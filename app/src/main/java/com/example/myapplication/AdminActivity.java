package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {
    private TextView welcomeText;
    private CardView appointmentsCard, ordersCard;
    private ListView listView;
    private Button btnLogout;
    private String currentView = "none";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        
        // Validate admin session
        if (!validateAdminSession()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        displayAppointments(); // Show appointments by default
    }

    private boolean validateAdminSession() {
        String username = sharedPreferences.getString("username", "");
        boolean isAdmin = sharedPreferences.getBoolean("isadmin", false);
        return !TextUtils.isEmpty(username) && isAdmin;
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.adminWelcomeText);
        appointmentsCard = findViewById(R.id.cardAppointments);
        ordersCard = findViewById(R.id.cardOrders);
        listView = findViewById(R.id.adminListView);
        btnLogout = findViewById(R.id.buttonAdminLogout);

        String username = sharedPreferences.getString("username", "Admin");
        welcomeText.setText("Welcome " + username);
    }

    private void setupClickListeners() {
        appointmentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentView.equals("appointments")) {
                    currentView = "appointments";
                    displayAppointments();
                }
            }
        });

        ordersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentView.equals("orders")) {
                    currentView = "orders";
                    displayOrders();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void displayAppointments() {
        Database db = new Database(this);
        ArrayList<String> list = db.getAllAppointments();
        ArrayList<HashMap<String, String>> items = new ArrayList<>();
        
        for (String appointment : list) {
            String[] parts = appointment.split("\\|");
            if (parts.length >= 5) {
                HashMap<String, String> item = new HashMap<>();
                item.put("line_a", "Patient: " + parts[0].trim());
                item.put("line_b", "Address: " + parts[1].trim());
                item.put("line_c", "Contact: " + parts[2].trim());
                item.put("line_d", "Username: " + parts[3].trim());
                item.put("line_e", "Date & Time: " + parts[4].trim());
                item.put("line_f", "");
                item.put("line_g", "");
                items.add(item);
            }
        }

        SimpleAdapter sa = new SimpleAdapter(
            this,
            items,
            R.layout.multi_lines,
            new String[] { "line_a", "line_b", "line_c", "line_d", "line_e", "line_f", "line_g" },
            new int[] { R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, 
                       R.id.line_f, R.id.line_g }
        );

        listView.setAdapter(sa);
    }

    private void displayOrders() {
        Database db = new Database(this);
        ArrayList<String> list = db.getAllOrders();
        ArrayList<HashMap<String, String>> items = new ArrayList<>();
        
        for (String order : list) {
            String[] parts = order.split("\\|");
            if (parts.length >= 7) {
                HashMap<String, String> item = new HashMap<>();
                item.put("line_a", "Customer: " + parts[0].trim());
                item.put("line_b", "Address: " + parts[1].trim());
                item.put("line_c", "Contact: " + parts[2].trim());
                item.put("line_d", "Username: " + parts[3].trim());
                item.put("line_e", "Date & Time: " + parts[4].trim());
                item.put("line_f", "Amount: " + parts[5].trim());
                item.put("line_g", "Type: " + parts[6].trim());
                items.add(item);
            }
        }

        SimpleAdapter sa = new SimpleAdapter(
            this,
            items,
            R.layout.multi_lines,
            new String[] { "line_a", "line_b", "line_c", "line_d", "line_e", "line_f", "line_g" },
            new int[] { R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, 
                       R.id.line_f, R.id.line_g }
        );

        listView.setAdapter(sa);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!validateAdminSession()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
