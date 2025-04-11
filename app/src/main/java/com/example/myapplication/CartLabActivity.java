package com.example.myapplication;

import static com.example.myapplication.R.id.buttonCartTime;

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
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_lab);
        initializeViews();
        setupListeners();
        displayCartItems();
        initDatePicker();
        initTimePicker();
    }

    private void initializeViews() {
        dateButton = findViewById(R.id.buttonBMCartDate);
        timeButton = findViewById(buttonCartTime);
        btnCheckout = findViewById(R.id.buttonBMCartDate);
        btnBack = findViewById(R.id.buttonBMCBack);
        lst = findViewById(R.id.listViewBMCart);
        tvTotal = findViewById(R.id.textViewBMCTotalCost);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            Intent it = new Intent(CartLabActivity.this, LabTestBookActivity.class);
            it.putExtra("price", tvTotal.getText().toString());
            it.putExtra("date", dateButton.getText().toString());
            it.putExtra("time", timeButton.getText().toString());
            startActivity(it);
        });

        dateButton.setOnClickListener(v -> datePickerDialog.show());

        timeButton.setOnClickListener(v -> timePickerDialog.show());
    }

    private void displayCartItems() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        Database db = new Database(getApplicationContext());

        float totalAmount = 0;
        ArrayList<String> dbData = db.getCartData(username, "Lab");
        list = new ArrayList<>();

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
                totalAmount += Float.parseFloat(parts[1]);
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
        tvTotal.setText("Total Cost: ₹" + totalAmount);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth1) -> dateSetListener.onDateSet(view, year1, month1, dayOfMonth1), year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() + 86400000);
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> timeButton.setText(hourOfDay + ":" + minute);

        Calendar cal = Calendar.getInstance();
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hrs, mins, true);
    }
}