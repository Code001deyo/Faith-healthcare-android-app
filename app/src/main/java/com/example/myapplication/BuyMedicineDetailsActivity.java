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

public class BuyMedicineDetailsActivity extends AppCompatActivity {
    private TextView tvFaithHealthcare;
    private TextView tvMedicineName;
    private TextView tvTotalCost;
    private EditText edDetails;
    private Button btnBack;
    private Button btnAddToCart;
    private String medicineName;
    private float medicinePrice;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine_details);

        initializeViews();
        setupDatabase();
        loadMedicineDetails();
        setupButtons();
    }

    private void initializeViews() {
        tvFaithHealthcare = findViewById(R.id.textViewFaithHealthcare);
        tvMedicineName = findViewById(R.id.textViewBMCartTitle);
        edDetails = findViewById(R.id.listViewBMCart);
        tvTotalCost = findViewById(R.id.textViewBMDTotalCost);
        btnBack = findViewById(R.id.buttonBMDBack);
        btnAddToCart = findViewById(R.id.buttonBMToCart);

        // Disable editing of details
        edDetails.setKeyListener(null);
    }

    private void setupDatabase() {
        db = new Database(this);
    }

    private void loadMedicineDetails() {
        Intent intent = getIntent();
        if (intent == null) {
            showError("Error loading medicine details");
            return;
        }

        medicineName = intent.getStringExtra("text1");
        String details = intent.getStringExtra("text2");
        String price = intent.getStringExtra("text3");

        if (TextUtils.isEmpty(medicineName) || TextUtils.isEmpty(details) || TextUtils.isEmpty(price)) {
            showError("Invalid medicine data");
            return;
        }

        try {
            medicinePrice = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            showError("Invalid price format");
            return;
        }

        tvFaithHealthcare.setText("Faith Healthcare");
        tvFaithHealthcare.setTextColor(0xFFFFFFFF);
        tvMedicineName.setText(medicineName);
        tvMedicineName.setTextColor(0xFFFFFFFF);
        edDetails.setText(details);
        edDetails.setTextColor(0xFF222222);
        edDetails.setBackgroundColor(0xFFFFFFFF);
        tvTotalCost.setText(String.format(Locale.getDefault(), "Total Cost: KES %.2f", medicinePrice));
        tvTotalCost.setTextColor(0xFFFFFFFF);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
        btnAddToCart.setTextColor(0xFFFFFFFF);
        btnAddToCart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
    }

    private void addToCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (TextUtils.isEmpty(username)) {
            showError("Please login to add items to cart");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (TextUtils.isEmpty(medicineName) || medicinePrice <= 0) {
            showError("Invalid medicine data");
            return;
        }

        try {
            if (db.checkCart(username, medicineName)) {
                showMessage("Medicine already in cart");
                return;
            }

            db.addCart(username, medicineName, medicinePrice, "medicine");
            showMessage("Medicine added to cart");
            
            Intent intent = new Intent(this, CartBuyMedicineActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            showError("Error adding to cart: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}