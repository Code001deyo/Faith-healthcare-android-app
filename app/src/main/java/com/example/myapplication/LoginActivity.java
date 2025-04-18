package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText edUsername, edPassword;
    private Button btnLogin;
    private TextView tvNewUser;
    private RadioGroup rgLoginType;
    private RadioButton rbPatient, rbAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupClickListeners();
        checkExistingLogin();
    }

    private void initializeViews() {
        edUsername = findViewById(R.id.editTextLoginUsername);
        edPassword = findViewById(R.id.editTextLoginPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        tvNewUser = findViewById(R.id.textViewNewUser);
        rgLoginType = findViewById(R.id.radioGroupLoginType);
        rbPatient = findViewById(R.id.radioButtonPatient);
        rbAdmin = findViewById(R.id.radioButtonAdmin);

        // Set patient as default selection
        rbPatient.setChecked(true);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });

        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rbAdmin.isChecked()) {
                    Toast.makeText(LoginActivity.this, "Admin registration is not allowed", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            }
        });

        rgLoginType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tvNewUser.setVisibility(checkedId == R.id.radioButtonPatient ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void handleLogin() {
        String username = edUsername.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }

        Database db = new Database(this);
        
        // Special handling for admin login
        if (rbAdmin.isChecked()) {
            if (username.equals("admin") && password.equals("admin123")) {
                // Save admin login state
                SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("username", username);
                editor.putBoolean("isadmin", true);
                editor.apply();

                Toast.makeText(this, "Admin Login Success", Toast.LENGTH_SHORT).show();
                
                // Start admin activity
                try {
                    Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish(); // Close login activity
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error starting admin activity", Toast.LENGTH_SHORT).show();
                }
                return;
            } else {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Handle regular user login
        int loginResult = db.login(username, password);
        Log.d("LoginActivity", "Login attempt result: " + loginResult);

        if (loginResult > 0) {
            // Save login state
            SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("username", username);
            editor.putBoolean("isadmin", false);
            editor.apply();

            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

            try {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Close login activity
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error starting home activity", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkExistingLogin() {
        SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
        if (sharedpreferences.contains("username")) {
            String username = sharedpreferences.getString("username", "");
            boolean isAdmin = sharedpreferences.getBoolean("isadmin", false);

            // Verify stored credentials
            Database db = new Database(this);
            int loginResult = db.login(username, "");  // We don't store passwords
            
            if (loginResult > 0 && ((loginResult == 2) == isAdmin)) {
                Intent intent;
                if (isAdmin) {
                    intent = new Intent(LoginActivity.this, AdminActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, HomeActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                // Clear invalid session
                sharedpreferences.edit().clear().apply();
            }
        }
    }
}