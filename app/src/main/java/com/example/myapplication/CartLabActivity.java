package com.example.myapplication;

import java.util.Locale;



import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;




public class CartLabActivity extends AppCompatActivity {
    HashMap<String, String> item;
    ArrayList<HashMap<String, String>> list;
    SimpleAdapter sa;
    TextView tvTotal;
    ListView lst;
    private Button dateButton, timeButton, btnBack, btnCheckout;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog; // Ensure all buttons and pickers are declared

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_lab);
        try {
            initializeViews();
            setupListeners();
            displayCartItems();
            initDatePicker();
            initTimePicker();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        tvTotal = findViewById(R.id.textViewBMCTotalCost);
        lst = findViewById(R.id.listViewBMCart);
        dateButton = findViewById(R.id.buttonBMCartDate);
        timeButton = findViewById(R.id.buttonBMCartTime);
        btnBack = findViewById(R.id.buttonBMCBack);
        btnCheckout = findViewById(R.id.buttonBMToCart);

        dateButton.setEnabled(true);
        dateButton.setClickable(true);
        timeButton.setEnabled(true);
        timeButton.setClickable(true);
        btnBack.setEnabled(true);
        btnBack.setClickable(true);
        btnCheckout.setEnabled(true);
        btnCheckout.setClickable(true);
    }

    private void setupListeners() {
        // Date picker
        if (dateButton != null) {
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (datePickerDialog != null) {
                        datePickerDialog.show();
                    } else {
                        Toast.makeText(CartLabActivity.this, "Date picker not initialized", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Time picker
        if (timeButton != null) {
            timeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (timePickerDialog != null) {
                        timePickerDialog.show();
                    } else {
                        Toast.makeText(CartLabActivity.this, "Time picker not initialized", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        // Checkout button
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list == null || list.isEmpty()) {
                        Toast.makeText(CartLabActivity.this, "Your lab cart is empty. Add items before checkout.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent it = new Intent(CartLabActivity.this, LabTestBookActivity.class);
                    it.putExtra("price", tvTotal.getText().toString().replace("Total Cost: ", ""));
                    it.putExtra("date", dateButton.getText().toString());
                    it.putExtra("time", timeButton.getText().toString());
                    startActivity(it);
                }
            });
        }
    }

    private void displayCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Please log in to view your cart.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Database db = new Database(getApplicationContext());
        float totalAmount = 0;
        ArrayList<String> dbData = null;
        try {
            dbData = db.getCartData(username, "Lab");
        } catch (Exception e) {
            Toast.makeText(this, "Error loading cart: " + e.getMessage(), Toast.LENGTH_LONG).show();
            dbData = new ArrayList<>();
        }
        list = new ArrayList<>();
        if (dbData == null || dbData.isEmpty()) {
            Toast.makeText(this, "Your lab cart is empty.", Toast.LENGTH_LONG).show();
        } else {
            for (String data : dbData) {
                String[] parts = data.split("\\$");
                if (parts.length >= 2) {
                    item = new HashMap<>();
                    item.put("line_a", parts[0]);
                    item.put("line_b", "Cost: ₹" + parts[1]);
                    item.put("line_c", "");
                    item.put("line_d", "");
                    item.put("line_e", "");
                    list.add(item);
                    try {
                        totalAmount += Float.parseFloat(parts[1]);
                    } catch (NumberFormatException nfe) {
                        // Ignore and continue
                    }
                }
            }
        }
        sa = new SimpleAdapter(
                this,
                list,
                R.layout.multi_lines,
                new String[]{"line_a", "line_b", "line_c", "line_d", "line_e"},
                new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e}
        );
        lst.setAdapter(sa);
        tvTotal.setText("Total Cost: Shillings " + totalAmount);
    }

    private void initDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                dateButton.setEnabled(true);
            }
        };

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        dateButton.setEnabled(true);
        dateButton.setClickable(true);
    }

    private void initTimePicker() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeButton.setText(time);
                timeButton.setEnabled(true);
            }
        };

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
        timeButton.setEnabled(true);
        timeButton.setClickable(true);
    }
}