package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.regex.Pattern;



public class AdminActivity extends AppCompatActivity {
    private TextView welcomeText;
    private LinearLayout appointmentsTable;
    private LinearLayout ordersTable;
    private LinearLayout usersTable;
    private CardView appointmentsCardView;
    private CardView ordersCardView;
    private CardView analyticsCardView;
    private CardView usersCardView;
    private CardView appointmentsCard;
    private CardView ordersCard;
    private Button btnLogout;
    private CardView cardAnalytics;
    private String currentView = "none";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin);

            sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            
            if (!validateAdminSession()) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }

            initializeViews();
            setupClickListeners();
            displayAppointments();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing admin panel", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private boolean validateAdminSession() {
        String username = sharedPreferences.getString("username", "");
        boolean isAdmin = sharedPreferences.getBoolean("isadmin", false);
        return !TextUtils.isEmpty(username) && isAdmin;
    }

    private void initializeViews() {
        // ... (existing code)
        analyticsCardView = findViewById(R.id.analyticsCardView);
        usersCardView = findViewById(R.id.usersCardView);
        usersTable = findViewById(R.id.usersTable);
        try {
            // Initialize views
            welcomeText = findViewById(R.id.adminWelcomeText);
            if (welcomeText == null) throw new IllegalStateException("adminWelcomeText not found");

            appointmentsCard = findViewById(R.id.cardAppointments);
            if (appointmentsCard == null) throw new IllegalStateException("cardAppointments not found");

            ordersCard = findViewById(R.id.cardOrders);
            if (ordersCard == null) throw new IllegalStateException("cardOrders not found");

            appointmentsTable = findViewById(R.id.appointmentsTable);
            if (appointmentsTable == null) throw new IllegalStateException("appointmentsTable not found");

            ordersTable = findViewById(R.id.ordersTable);
            if (ordersTable == null) throw new IllegalStateException("ordersTable not found");

            ordersCardView = findViewById(R.id.ordersCardView);
            if (ordersCardView == null) throw new IllegalStateException("ordersCardView not found");

            btnLogout = findViewById(R.id.buttonAdminLogout);
            if (btnLogout == null) throw new IllegalStateException("buttonAdminLogout not found");

            cardAnalytics = findViewById(R.id.cardAnalytics);
            if (cardAnalytics == null) throw new IllegalStateException("cardAnalytics not found");

            // Set welcome text
            String username = sharedPreferences.getString("username", "Admin");
            welcomeText.setText("Welcome " + username);

            // Initialize visibility
            appointmentsTable.setVisibility(View.VISIBLE);
            ordersCardView.setVisibility(View.GONE);
            currentView = "appointments";

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupClickListeners() {
        try {
            appointmentsCard.setOnClickListener(v -> {
                if (!currentView.equals("appointments")) {
                    currentView = "appointments";
                    appointmentsTable.setVisibility(View.VISIBLE);
                    ordersCardView.setVisibility(View.GONE);
                    analyticsCardView.setVisibility(View.GONE);
                    usersCardView.setVisibility(View.GONE);
                    displayAppointments();
                }
            });

            ordersCard.setOnClickListener(v -> {
                if (!currentView.equals("orders")) {
                    currentView = "orders";
                    appointmentsTable.setVisibility(View.GONE);
                    ordersCardView.setVisibility(View.VISIBLE);
                    analyticsCardView.setVisibility(View.GONE);
                    usersCardView.setVisibility(View.GONE);
                    displayOrders();
                }
            });

            btnLogout.setOnClickListener(v -> logout());

            cardAnalytics.setOnClickListener(v -> {
                if (!currentView.equals("analytics")) {
                    currentView = "analytics";
                    appointmentsTable.setVisibility(View.GONE);
                    ordersCardView.setVisibility(View.GONE);
                    analyticsCardView.setVisibility(View.VISIBLE);
                    usersCardView.setVisibility(View.GONE);
                    displayAnalytics();
                }
            });

            // Users card click
            CardView cardUsers = findViewById(R.id.cardUsers);
            if (cardUsers != null) {
                cardUsers.setOnClickListener(v -> {
                    if (!currentView.equals("users")) {
                        currentView = "users";
                        appointmentsTable.setVisibility(View.GONE);
                        ordersCardView.setVisibility(View.GONE);
                        analyticsCardView.setVisibility(View.GONE);
                        usersCardView.setVisibility(View.VISIBLE);
                        displayUsers();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error setting up click listeners", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayUsers() {
        Database db = new Database(this);
        ArrayList<String> users = db.getAllUsers();
        usersTable.removeViews(1, Math.max(0, usersTable.getChildCount() - 1)); // Keep title
        if (users.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No registered users.");
            empty.setPadding(8, 16, 8, 8);
            usersTable.addView(empty);
        } else {
            for (String user : users) {
                String[] parts = user.split("\\$");
                String username = parts[0];
                String email = parts.length > 1 ? parts[1] : "";

                // Card-style row
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(12, 12, 12, 12);
                row.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 12, 0, 0);
                row.setLayoutParams(rowParams);

                // Info column
                LinearLayout infoCol = new LinearLayout(this);
                infoCol.setOrientation(LinearLayout.VERTICAL);
                infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                TextView tvUsername = new TextView(this);
                tvUsername.setText("Username: " + username);
                tvUsername.setTextSize(16);
                tvUsername.setTypeface(null, Typeface.BOLD);
                tvUsername.setTextColor(getResources().getColor(android.R.color.black));
                tvUsername.setPadding(0, 0, 0, 2);

                TextView tvEmail = new TextView(this);
                tvEmail.setText("Email: " + email);
                tvEmail.setTextSize(15);
                tvEmail.setTextColor(getResources().getColor(android.R.color.darker_gray));
                tvEmail.setPadding(0, 0, 0, 2);

                infoCol.addView(tvUsername);
                infoCol.addView(tvEmail);

                // Delete button
                Button btnDelete = new Button(this);
                btnDelete.setText("Delete");
                btnDelete.setTextSize(14);
                btnDelete.setAllCaps(false);
                btnDelete.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                btnDelete.setTextColor(getResources().getColor(android.R.color.white));
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                btnParams.setMargins(24, 0, 0, 0);
                btnDelete.setLayoutParams(btnParams);
                btnDelete.setOnClickListener(v -> {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setTitle("Delete User");
                    builder.setMessage("Are you sure you want to delete user '" + username + "'?");
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        db.deleteUser(username);
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        displayUsers();
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                });

                row.addView(infoCol);
                row.addView(btnDelete);
                usersTable.addView(row);
            }
        }
    }

    private void displayAnalytics() {
        Database db = new Database(this);
        int totalAppointments = db.getAllAppointments().size();
        int totalOrders = db.getAllOrders().size();
        String analysis;
        if (totalAppointments == 0 && totalOrders == 0) {
            analysis = "No appointments or orders yet.";
        } else if (totalAppointments > totalOrders) {
            analysis = "More appointments than orders. Ratio: " + String.format("%.2f", (double)totalAppointments/(totalOrders == 0 ? 1 : totalOrders));
        } else if (totalOrders > totalAppointments) {
            analysis = "More orders than appointments. Ratio: " + String.format("%.2f", (double)totalOrders/(totalAppointments == 0 ? 1 : totalAppointments));
        } else {
            analysis = "Appointments and orders are equal.";
        }
        TextView analyticsSummary = analyticsCardView.findViewById(R.id.analyticsSummaryText);
        String message = "Total Appointments: " + totalAppointments + "\n" +
                "Total Orders: " + totalOrders + "\n\n" +
                "Analysis: " + analysis;
        analyticsSummary.setText(message);
    }


    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void displayAppointments() {
        try {
            Database db = new Database(this);
            ArrayList<String> list = db.getAllAppointments();
            
            // Clear existing content
            appointmentsTable.removeAllViews();
            
            if (list.isEmpty()) {
                TextView noAppointmentsText = new TextView(this);
                noAppointmentsText.setText("No appointments found");
                noAppointmentsText.setTextSize(16);
                noAppointmentsText.setTypeface(null, Typeface.BOLD);
                noAppointmentsText.setTextColor(getResources().getColor(android.R.color.black));
                noAppointmentsText.setPadding(16, 16, 16, 16);
                noAppointmentsText.setGravity(android.view.Gravity.CENTER);
                noAppointmentsText.setBackgroundColor(getResources().getColor(android.R.color.white));
                appointmentsTable.addView(noAppointmentsText);
                return;
            }
            
            for (String appointment : list) {
                try {
                    String[] parts = appointment.split(Pattern.quote("$"));
                    if (parts.length >= 6) {
                        // Create a container for each appointment
                        CardView appointmentCard = new CardView(this);
                        appointmentCard.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        appointmentCard.setCardElevation(4);
                        appointmentCard.setRadius(8);
                        appointmentCard.setUseCompatPadding(true);
                        appointmentCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                        
                        LinearLayout appointmentContainer = new LinearLayout(this);
                        appointmentContainer.setOrientation(LinearLayout.VERTICAL);
                        appointmentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        appointmentContainer.setPadding(16, 16, 16, 16);
                        
                        String id = parts[0].trim();
                        String fullname = parts[1].trim();
                        String address = parts[2].trim();
                        String contact = parts[3].trim();
                        String date = parts[4].trim();
                        String time = parts[5].trim();
                        
                        // Add details in vertical layout
                        addDetailTextView(appointmentContainer, "Name", fullname);
                        addDetailTextView(appointmentContainer, "Address", address);
                        addDetailTextView(appointmentContainer, "Contact", contact);
                        addDetailTextView(appointmentContainer, "Date", date);
                        addDetailTextView(appointmentContainer, "Time", time);
                        
                        // Add buttons in horizontal layout
                        LinearLayout buttonContainer = new LinearLayout(this);
                        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
                        buttonContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        buttonContainer.setPadding(0, 16, 0, 0);
                        
                        Button acceptBtn = new Button(this);
                        acceptBtn.setText("Accept");
                        acceptBtn.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        final String appointmentId = id;
                        acceptBtn.setOnClickListener(v -> {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Accept Appointment")
                                .setMessage("Are you sure you want to accept this appointment?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    try {
                                        if (db.acceptAppointment(appointmentId)) {
                                            Toast.makeText(this, "Appointment accepted", Toast.LENGTH_SHORT).show();
                                            displayAppointments();
                                        } else {
                                            Toast.makeText(this, "Error accepting appointment", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        });

                        Button rejectBtn = new Button(this);
                        rejectBtn.setText("Reject");
                        rejectBtn.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        rejectBtn.setOnClickListener(v -> {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Reject Appointment")
                                .setMessage("Are you sure you want to reject this appointment?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    try {
                                        if (db.deleteAppointment(id)) {
                                            Toast.makeText(this, "Appointment rejected", Toast.LENGTH_SHORT).show();
                                            displayAppointments();
                                        } else {
                                            Toast.makeText(this, "Error rejecting appointment", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        });

                        buttonContainer.addView(acceptBtn);
                        buttonContainer.addView(rejectBtn);
                        appointmentContainer.addView(buttonContainer);
                        
                        appointmentCard.addView(appointmentContainer);
                        appointmentsTable.addView(appointmentCard);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error displaying appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Continue with next appointment
                }
            }
            
            // Update visibility
            appointmentsTable.setVisibility(View.VISIBLE);
            ordersCardView.setVisibility(View.GONE);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying appointments", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayOrders() {
        try {
            Database db = new Database(this);
            ArrayList<String> list = db.getAllOrders();
            
            // Clear existing content
            ordersTable.removeAllViews();
            
            if (list.isEmpty()) {
                TextView noOrdersText = new TextView(this);
                noOrdersText.setText("No orders found");
                noOrdersText.setTextSize(16);
                noOrdersText.setTypeface(null, Typeface.BOLD);
                noOrdersText.setTextColor(getResources().getColor(android.R.color.black));
                noOrdersText.setPadding(16, 16, 16, 16);
                noOrdersText.setGravity(android.view.Gravity.CENTER);
                ordersTable.addView(noOrdersText);
                return;
            }
            
            for (String order : list) {
                try {
                    String[] parts = order.split(Pattern.quote("$"));
                    if (parts.length >= 7) {
                        // Create a container for each order
                        CardView orderCard = new CardView(this);
                        orderCard.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        orderCard.setCardElevation(4);
                        orderCard.setRadius(8);
                        orderCard.setUseCompatPadding(true);
                        orderCard.setCardBackgroundColor(getResources().getColor(android.R.color.white));
                        
                        LinearLayout orderContainer = new LinearLayout(this);
                        orderContainer.setOrientation(LinearLayout.VERTICAL);
                        orderContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        orderContainer.setPadding(16, 16, 16, 16);
                        
                        String id = parts[0].trim();
                        String customer = parts[1].trim();
                        String address = parts[2].trim();
                        String contact = parts[3].trim();
                        String date = parts[4].trim();
                        String time = parts[5].trim();
                        String amount = parts[6].trim();
                        
                        // Add details in vertical layout
                        addDetailTextView(orderContainer, "Name", customer);
                        addDetailTextView(orderContainer, "Address", address);
                        addDetailTextView(orderContainer, "Contact", contact);
                        addDetailTextView(orderContainer, "Date", date);
                        addDetailTextView(orderContainer, "Time", time);
                        addDetailTextView(orderContainer, "Amount", amount);
                        
                        // Add buttons in horizontal layout
                        LinearLayout buttonContainer = new LinearLayout(this);
                        buttonContainer.setOrientation(LinearLayout.HORIZONTAL);
                        buttonContainer.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                        buttonContainer.setPadding(0, 16, 0, 0);
                        
                        Button deleteBtn = new Button(this);
                        deleteBtn.setText("Delete Order");
                        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        
                        final String orderId = id;
                        deleteBtn.setOnClickListener(v -> {
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                .setTitle("Delete Order")
                                .setMessage("Are you sure you want to delete this order?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    try {
                                        if (db.deleteOrder(orderId)) {
                                            Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show();
                                            displayOrders();
                                        } else {
                                            Toast.makeText(this, "Error deleting order", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        });
                        buttonContainer.addView(deleteBtn);
                        
                        orderContainer.addView(buttonContainer);
                        orderCard.addView(orderContainer);
                        ordersTable.addView(orderCard);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error displaying order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            // Update visibility
            appointmentsTable.setVisibility(View.GONE);
            ordersCardView.setVisibility(View.VISIBLE);
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying orders", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDetailTextView(LinearLayout container, String label, String value) {
        TextView tv = new TextView(this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
        tv.setPadding(0, 4, 0, 4);
        tv.setTextColor(getResources().getColor(android.R.color.black));

        String displayText = label + ": " + value;
        tv.setText(displayText);

        if (label.equals("Name")) {
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(16);
        } else {
            tv.setTextSize(14);
        }

        container.addView(tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!validateAdminSession()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
