package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText edUsername, edEmail, edPassword, edConfirm;
    private Button btn;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        edUsername = findViewById(R.id.editTextRegUsername);
        edEmail = findViewById(R.id.editTextRegEmail);
        edPassword = findViewById(R.id.editTextRegPassword);
        edConfirm = findViewById(R.id.editTextRegConfirmPassword);
        btn = findViewById(R.id.buttonRegister);
        tv = findViewById(R.id.textViewExistingUser);
    }

    private void setupClickListeners() {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRegistration();
            }
        });
    }

    private void handleRegistration() {
        String username = edUsername.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        String confirm = edConfirm.getText().toString().trim();

        if (!validateInputs(username, email, password, confirm)) {
            return;
        }

        // Try to register
        Database db = new Database(RegisterActivity.this);
        if (db.register(username, email, password)) {
            Log.d("RegisterActivity", "Registration successful for user: " + username);
            Toast.makeText(RegisterActivity.this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        } else {
            Log.d("RegisterActivity", "Registration failed for user: " + username);
            Toast.makeText(RegisterActivity.this, "Registration failed. Username may already exist.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String username, String email, String password, String confirm) {
        // Check for empty fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || 
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Username validation
        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.equalsIgnoreCase("admin")) {
            Toast.makeText(this, "This username is not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Password validation
        if (!password.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPassword(password)) {
            Toast.makeText(this, 
                "Password must be at least 8 characters long and contain letters, numbers, and special characters", 
                Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (!Character.isWhitespace(c)) {
                hasSpecial = true;
            }
        }

        return hasLetter && hasDigit && hasSpecial;
    }
}
