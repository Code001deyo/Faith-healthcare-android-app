package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class BookAppointmentActivity extends AppCompatActivity {

    EditText ed1,ed2,ed3,ed4;
    TextView tv;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton,timeButton,btnBook,btnBack;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        tv=findViewById(R.id.textViewBMBAppTitle);
        ed1=findViewById(R.id.editTextLTBMBFullName);
        ed2=findViewById(R.id.editTextLTBMBAddress);
        ed3=findViewById(R.id.editTextLTBMBContact);
        ed4=findViewById(R.id.editTextLTBMBPinCode);
        dateButton=findViewById(R.id.buttonBMCartDate);
        timeButton=findViewById(R.id.buttonAppTime);
        btnBook = findViewById(R.id.buttonBookAppointment);
        btnBack = findViewById(R.id.buttonLTBMBBooking);

        ed1.setKeyListener(null);
        ed2.setKeyListener(null);
        ed3.setKeyListener(null);
        ed4.setKeyListener(null);

        Intent it = getIntent();
        String title = it.getStringExtra("text1");
        String fullname = it.getStringExtra("text2");
        String address = it.getStringExtra("text3");
        String contact = it.getStringExtra("text4");
        String fees = it.getStringExtra("text5");

        tv.setText(title);
        ed1.setText(fullname);
        ed2.setText(address);
        ed3.setText(contact);
        ed4.setText("Cons Fees :"+ fees+"/-");

        initDatePicker();
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        initTimePicker();
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Validate date and time selection
                    if (dateButton.getText().toString().equals("Select Date") || 
                        timeButton.getText().toString().equals("Select Time")) {
                        Toast.makeText(getApplicationContext(), 
                            "Please select both date and time", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Database db = new Database(getApplicationContext());
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                    String username = sharedPreferences.getString("username", "");

                    if (username.isEmpty()) {
                        Toast.makeText(getApplicationContext(), 
                            "Please login first", 
                            Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BookAppointmentActivity.this, LoginActivity.class));
                        finish();
                        return;
                    }

                    if (db.checkAppointmentExists(username, 
                            title + "=>" + fullname, 
                            address, 
                            contact, 
                            dateButton.getText().toString(),
                            timeButton.getText().toString())) {
                        Toast.makeText(getApplicationContext(), 
                            "Appointment already booked", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        // Parse fees safely
                        float feesValue;
                        try {
                            feesValue = Float.parseFloat(fees);
                        } catch (NumberFormatException e) {
                            feesValue = 0.0f;
                        }

                        db.addOrder(username,
                            title + "=>" + fullname, 
                            address, 
                            contact,
                            0, 
                            dateButton.getText().toString(),
                            timeButton.getText().toString(),
                            feesValue,
                            "appointment");

                        Toast.makeText(getApplicationContext(), 
                            "Your appointment is done successfully", 
                            Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(BookAppointmentActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), 
                        "Error booking appointment: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                dateButton.setText(String.format("%02d/%02d/%d", dayOfMonth, month, year));
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() + 86400000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                timeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        };
        Calendar cal = Calendar.getInstance();
        int hrs = cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);
        int style = AlertDialog.THEME_HOLO_DARK;
        timePickerDialog = new TimePickerDialog(this, style, timeSetListener, hrs, mins, true);
    }
}