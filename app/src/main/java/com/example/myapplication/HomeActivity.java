package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");

        // Check if user is logged in
        if (TextUtils.isEmpty(username) || sharedPreferences.getBoolean("isadmin", false)) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Set welcome message
        TextView welcomeText = findViewById(R.id.welcomeText);
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + username);
        } else {
            Toast.makeText(this, "Welcome, " + username, Toast.LENGTH_SHORT).show();
        }

        // Initialize all card views
        initializeCardViews();
    }

    private void initializeCardViews() {
        // Find Doctor Card
        setupCardView(R.id.cardFindDoctor, FindDoctorActivity.class);

        // Lab Test Card
        setupCardView(R.id.cardLabTest, LabTestActivity.class);

        // Order Details Card
        setupCardView(R.id.cardOrderDetails, OrderDetailsActivity.class);

        // Buy Medicine Card
        setupCardView(R.id.cardBuyMedicine, BuyMedicineActivity.class);

        // Health Articles Card
        setupCardView(R.id.cardHealthDoctor, HealthArticlesActivity.class);

        // Exit Card
        CardView exitCard = findViewById(R.id.cardExit);
        if (exitCard != null) {
            exitCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });
        }
    }

    private void setupCardView(int cardId, final Class<?> targetActivity) {
        CardView cardView = findViewById(cardId);
        if (cardView != null) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomeActivity.this, targetActivity));
                }
            });
        }
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is still logged in
        if (TextUtils.isEmpty(sharedPreferences.getString("username", ""))) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // Show a toast message to inform the user to use the exit button
        Toast.makeText(this, "Please use the Exit button to logout", Toast.LENGTH_SHORT).show();
    }
}