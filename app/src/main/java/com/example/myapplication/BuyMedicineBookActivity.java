package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Pattern;

public class BuyMedicineBookActivity extends AppCompatActivity {
    private EditText edName, edAddress, edContact, edPincode;
    private TextView tvTotalAmount, tvDeliveryDate;
    private Button btnBooking, btnBack;
    private float totalAmount;
    private String deliveryDate;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine_book);

        initializeViews();
        setupDatabase();
        loadOrderDetails();
        setupButtons();
    }

    private void initializeViews() {
        edName = findViewById(R.id.editTextLTBMBFullName);
        edAddress = findViewById(R.id.editTextLTBMBAddress);
        edContact = findViewById(R.id.editTextLTBMBContact);
        edPincode = findViewById(R.id.editTextLTBMBPinCode);
        tvTotalAmount = findViewById(R.id.textViewBMBTotalCost);
        tvDeliveryDate = findViewById(R.id.textViewBMBDeliveryDate);
        btnBooking = findViewById(R.id.buttonLTBMBBooking);
        btnBack = findViewById(R.id.buttonBMBBack);
    }

    private void setupDatabase() {
        db = new Database(this);
    }

    private void loadOrderDetails() {
        Intent intent = getIntent();
        if (intent == null) {
            showError("Error loading order details");
            finish();
            return;
        }

        String price = intent.getStringExtra("price");
        deliveryDate = intent.getStringExtra("date");

        if (TextUtils.isEmpty(price) || TextUtils.isEmpty(deliveryDate)) {
            showError("Invalid order details");
            finish();
            return;
        }

        try {
            // First try to parse as direct float
            try {
                totalAmount = Float.parseFloat(price);
            } catch (NumberFormatException e) {
                // If that fails, try to parse from "Price: X" format
                String[] priceParts = price.split(Pattern.quote(":"));
                if (priceParts.length < 2) {
                    throw new IllegalArgumentException("Invalid price format");
                }
                totalAmount = Float.parseFloat(priceParts[1].trim().replace("KES", "").trim());
            }
            
            tvTotalAmount.setText(String.format(Locale.getDefault(), "Total Amount: KES %.2f", totalAmount));
            tvDeliveryDate.setText(String.format("Delivery Date: %s", deliveryDate));
        } catch (Exception e) {
            showError("Error processing price: " + e.getMessage());
            finish();
        }
    }

    private void setupButtons() {
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void processOrder() {
        if (!validateInputs()) {
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (TextUtils.isEmpty(username)) {
            showError("Please login to place order");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        try {
            db.addOrder(
                username,
                edName.getText().toString().trim(),
                edAddress.getText().toString().trim(),
                edContact.getText().toString().trim(),
                Integer.parseInt(edPincode.getText().toString().trim()),
                deliveryDate,
                "NA", // No time needed for medicine delivery
                totalAmount,
                "medicine"
            );

            db.removeCart(username, "medicine");
            showSuccess("Your order has been placed successfully");

            // Clear back stack and go to home
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            showError("Error placing order: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        // Reset errors
        edName.setError(null);
        edAddress.setError(null);
        edContact.setError(null);
        edPincode.setError(null);

        String name = edName.getText().toString().trim();
        String address = edAddress.getText().toString().trim();
        String contact = edContact.getText().toString().trim();
        String pincode = edPincode.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            edName.setError("Please enter your full name");
            edName.requestFocus();
            return false;
        }

        if (name.length() < 3) {
            edName.setError("Name must be at least 3 characters");
            edName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(address)) {
            edAddress.setError("Please enter your address");
            edAddress.requestFocus();
            return false;
        }

        if (address.length() < 10) {
            edAddress.setError("Please enter a complete address");
            edAddress.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(contact)) {
            edContact.setError("Please enter your contact number");
            edContact.requestFocus();
            return false;
        }

        if (!contact.matches("\\d{10}")) {
            edContact.setError("Please enter a valid 10-digit contact number");
            edContact.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(pincode)) {
            edPincode.setError("Please enter your pincode");
            edPincode.requestFocus();
            return false;
        }

        if (!pincode.matches("\\d{6}")) {
            edPincode.setError("Please enter a valid 6-digit pincode");
            edPincode.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}