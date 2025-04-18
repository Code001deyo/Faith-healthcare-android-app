package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class CartBuyMedicineActivity extends AppCompatActivity {
    private TextView tvTotal;
    private ListView cartListView;
    private DatePickerDialog datePickerDialog;
    private Button dateButton, btnCheckout, btnBack;
    private ArrayList<HashMap<String, String>> cartList;
    private CartMedicineListAdapter adapter;
    private float totalAmount = 0;
    private String[][] cartItems = {};
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_buy_medicine);

        initializeViews();
        loadCartData();
        setupButtons();
        initDatePicker();
    }

    private void initializeViews() {
        dateButton = findViewById(R.id.buttonBMCartDate);
        btnCheckout = findViewById(R.id.buttonBMToCart);
        btnBack = findViewById(R.id.buttonBMCBack);
        tvTotal = findViewById(R.id.textViewBMCTotalCost);
        cartListView = findViewById(R.id.listViewBMCart);

        // Set default date
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        dateButton.setText(dateFormat.format(new Date()));
    }

    private void loadCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please login to view cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Database db = new Database(this);
        ArrayList<String> dbData = db.getCartData(username, "medicine");

        if (dbData.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            btnCheckout.setEnabled(false);
            return;
        }

        processCartData(dbData);
        updateCartDisplay();
    }

    private void processCartData(ArrayList<String> dbData) {
        cartItems = new String[dbData.size()][];
        for (int i = 0; i < cartItems.length; i++) {
            cartItems[i] = new String[5];
        }

        totalAmount = 0;
        for (int i = 0; i < dbData.size(); i++) {
            String[] strData = dbData.get(i).toString().split(Pattern.quote("$"));
            if (strData.length >= 2) {
                cartItems[i][0] = strData[0];
                cartItems[i][4] = "Cost: KES " + strData[1];
                try {
                    totalAmount += Float.parseFloat(strData[1]);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Error processing price for " + strData[0], Toast.LENGTH_SHORT).show();
                }
            }
        }

        tvTotal.setText(String.format(Locale.getDefault(), "Total Cost: KES %.2f", totalAmount));
    }

    private void updateCartDisplay() {
        cartList = new ArrayList<>();
        for (String[] item : cartItems) {
            HashMap<String, String> map = new HashMap<>();
            map.put("line1", item[0]); // Medicine name
            map.put("line2", item[1]); // Description (if any)
            map.put("line3", item[2]); // Additional info (if any)
            map.put("line4", item[3]); // Additional info (if any)
            map.put("line5", item[4]); // Cost
            // Set image resource if available (reuse logic from BuyMedicineActivity)
            Integer resId = BuyMedicineActivity.medicineImageMap.get(item[0]);
            if (resId != null) {
                map.put("imageResId", String.valueOf(resId));
            }
            cartList.add(map);
        }

        adapter = new CartMedicineListAdapter(
            this,
            cartList
        );
        cartListView.setAdapter(adapter);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalAmount <= 0) {
                    Toast.makeText(CartBuyMedicineActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dateButton.getText().toString().equals("Select Date")) {
                    Toast.makeText(CartBuyMedicineActivity.this, "Please select a delivery date", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CartBuyMedicineActivity.this, BuyMedicineBookActivity.class);
                intent.putExtra("price", String.format(Locale.getDefault(), "Price: KES %.2f", totalAmount));
                intent.putExtra("date", dateButton.getText().toString());
                startActivity(intent);
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month, year);
            dateButton.setText(date);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis() + 30L * 24 * 60 * 60 * 1000); // Max 30 days ahead
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartData(); // Refresh cart data when returning to this activity
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
