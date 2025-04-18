package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LabTestBookActivity extends AppCompatActivity {

    EditText edname,edaddress,edcontact,edpincode;
    Button btnBooking;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_book);

        edname = findViewById(R.id.editTextLTBMBFullName);
        edaddress = findViewById(R.id.editTextLTBMBAddress);
        edcontact = findViewById(R.id.editTextLTBMBContact);
        edpincode = findViewById(R.id.editTextLTBMBPinCode);
        btnBooking = findViewById(R.id.buttonLTBMBBooking);

        Intent intent = getIntent();
        String[] price = intent.getStringExtra("price").toString().split(java.util.regex.Pattern.quote(":"));
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username","").toString();
                Database db = new Database(getApplicationContext());
                String fullname = edname.getText().toString();
                String address = edaddress.getText().toString();
                String contact = edcontact.getText().toString();
                int pincode = Integer.parseInt(edpincode.getText().toString());
                float amount = Float.parseFloat(price[1].toString());
                // Add order (otype = 'lab')
                boolean orderSuccess = db.addOrder(username, fullname, address, contact, pincode, date, time, amount, "lab");
                // Add appointment
                boolean appointmentSuccess = db.addAppointment(username, fullname, address, contact, date, time);
                // Remove from lab cart
                db.removeCart(username, "Lab");
                if (orderSuccess && appointmentSuccess) {
                    Toast.makeText(getApplicationContext(), "Your Lab Test Booking is Done Successfully", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LabTestBookActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Error booking lab test. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}