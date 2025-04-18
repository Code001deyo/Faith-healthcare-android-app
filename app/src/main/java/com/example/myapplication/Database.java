package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    // ...
    /**
     * Returns a list of all users (excluding admin), each as "username$email".
     */
    public ArrayList<String> getAllUsers() {
        ArrayList<String> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query("users", new String[]{"username", "email"}, "isadmin=0", null, null, null, "username ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String username = cursor.getString(cursor.getColumnIndex("username"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    users.add(username + "$" + email);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error getting all users: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return users;
    }

    /**
     * Deletes a user by username. Returns true if a user was deleted.
     */
    public boolean deleteUser(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            int rows = db.delete("users", "username=?", new String[]{username});
            Log.d("Database", "Deleted user: " + username + ", rows: " + rows);
            return rows > 0;
        } catch (Exception e) {
            Log.e("Database", "Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@healthcare.com";
    private static final String DATABASE_NAME = "healthcare";
    private static final int DATABASE_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Only initialize admin user
        SQLiteDatabase db = getWritableDatabase();
        initializeAdmin(db);
    }

    private void initializeAdmin(SQLiteDatabase db) {
        // Check if admin exists
        Cursor cursor = null;
        try {
            String[] columns = {"username"};
            String selection = "username=?";
            String[] selectionArgs = {ADMIN_USERNAME};
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            
            if (cursor == null || !cursor.moveToFirst()) {
                // Create admin account if it doesn't exist
                ContentValues cv = new ContentValues();
                cv.put("username", ADMIN_USERNAME);
                cv.put("email", ADMIN_EMAIL);
                cv.put("password", ADMIN_PASSWORD);
                cv.put("isadmin", 1);
                long result = db.insert("users", null, cv);
                Log.d("Database", "Admin account created: " + (result != -1));
            }
        } catch (Exception e) {
            Log.e("Database", "Error initializing admin: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {

            // Create tables
            String qry1 = "CREATE TABLE users (" +
                "username TEXT PRIMARY KEY," +
                "email TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "isadmin INTEGER DEFAULT 0" +
                ")";
            sqLiteDatabase.execSQL(qry1);

            String qry2 = "CREATE TABLE cart (" +
                "username TEXT," +
                "product TEXT," +
                "price REAL," +
                "otype TEXT," +
                "FOREIGN KEY(username) REFERENCES users(username) ON DELETE CASCADE" +
                ")";
            sqLiteDatabase.execSQL(qry2);

            String qry3 = "CREATE TABLE orderplace (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "fullname TEXT," +
                "address TEXT," +
                "contactno TEXT," +
                "pincode INTEGER," +
                "date TEXT," +
                "time TEXT," +
                "amount REAL," +
                "otype TEXT," +
                "FOREIGN KEY(username) REFERENCES users(username) ON DELETE CASCADE" +
                ")";
            sqLiteDatabase.execSQL(qry3);

            String qry4 = "CREATE TABLE appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "fullname TEXT," +
                "address TEXT," +
                "contactno TEXT," +
                "date TEXT," +
                "time TEXT," +
                "FOREIGN KEY(username) REFERENCES users(username) ON DELETE CASCADE" +
                ")";
            sqLiteDatabase.execSQL(qry4);

        // --- AddAppointment method ---
        // See bottom of file for implementation


            Log.d("Database", "Tables created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }

    public boolean register(String username, String email, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            // Check if username already exists
            Cursor cursor = db.query("users", new String[]{"username"}, 
                "username=?", new String[]{username}, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                cursor.close();
                return false; // Username already exists
            }
            if (cursor != null) {
                cursor.close();
            }

            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("email", email);
            cv.put("password", password);
            cv.put("isadmin", 0);
            long result = db.insert("users", null, cv);
            Log.d("Database", "User registration result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int login(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Log.d("Database", "Login failed: Empty username or password");
            return 0;
        }

        // Special handling for admin login
        if (ADMIN_USERNAME.equals(username)) {
            if (ADMIN_PASSWORD.equals(password)) {
                Log.d("Database", "Admin login successful");
                return 2; // Admin login successful
            }
            Log.d("Database", "Admin login failed: incorrect password");
            return 0; // Admin login failed
        }

        // Regular user login
        int result = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String[] columns = {"password"};
            String selection = "username=? AND isadmin=0";
            String[] selectionArgs = {username};
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
                if (password.equals(storedPassword)) {
                    result = 1; // Regular user login successful
                    Log.d("Database", "User login successful: " + username);
                }
            }
        } catch (Exception e) {
            Log.e("Database", "Error during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        Log.d("Database", "Login result for " + username + ": " + result);
        return result;
    }

    public ArrayList<String> getAllAppointments() {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        
        try {
            c = db.query("orderplace", null, "otype=?", new String[]{"appointment"}, null, null, "date ASC, time ASC");
            
            if (c != null && c.moveToFirst()) {
                int idIndex = c.getColumnIndex("id");
                int fullnameIndex = c.getColumnIndex("fullname");
                int addressIndex = c.getColumnIndex("address");
                int contactnoIndex = c.getColumnIndex("contactno");
                int dateIndex = c.getColumnIndex("date");
                int timeIndex = c.getColumnIndex("time");

                do {
                    String appointment = String.format("%s$%s$%s$%s$%s$%s",
                        c.getString(idIndex),
                        c.getString(fullnameIndex),
                        c.getString(addressIndex),
                        c.getString(contactnoIndex),
                        c.getString(dateIndex),
                        c.getString(timeIndex)
                    );
                    arr.add(appointment);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error getting all appointments: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return arr;
    }

    public ArrayList<String> getAllOrders() {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        
        try {
            c = db.query("orderplace", null, "otype=?", new String[]{"medicine"}, null, null, "date ASC, time ASC");
            
            if (c != null && c.moveToFirst()) {
                int idIndex = c.getColumnIndex("id");
                int fullnameIndex = c.getColumnIndex("fullname");
                int addressIndex = c.getColumnIndex("address");
                int contactnoIndex = c.getColumnIndex("contactno");
                int dateIndex = c.getColumnIndex("date");
                int timeIndex = c.getColumnIndex("time");
                int amountIndex = c.getColumnIndex("amount");

                do {
                    String order = String.format("%s$%s$%s$%s$%s$%s$KES %.2f",
                        c.getString(idIndex),
                        c.getString(fullnameIndex),
                        c.getString(addressIndex),
                        c.getString(contactnoIndex),
                        c.getString(dateIndex),
                        c.getString(timeIndex),
                        c.getDouble(amountIndex)
                    );
                    arr.add(order);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error getting all orders: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return arr;
    }

    public boolean addCart(String username, String product, float price, String otype) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(product) || TextUtils.isEmpty(otype)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("product", product);
            cv.put("price", price);
            cv.put("otype", otype);
            long result = db.insert("cart", null, cv);
            Log.d("Database", "Cart addition result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error adding to cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean checkCart(String username, String product) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(product)) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String[] columns = {"username"};
            String selection = "username=? AND product=?";
            String[] selectionArgs = {username, product};
            c = db.query("cart", columns, selection, selectionArgs, null, null, null);
            boolean result = c != null && c.moveToFirst();
            Log.d("Database", "Cart check result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("Database", "Error checking cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        }
    }

    public void removeCart(String username, String otype) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(otype)) {
            return;
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            String whereClause = "username=? AND otype=?";
            String[] whereArgs = {username, otype};
            db.delete("cart", whereClause, whereArgs);
            Log.d("Database", "Cart removal result: " + whereArgs);
        } catch (Exception e) {
            Log.e("Database", "Error removing from cart: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public ArrayList<String> getCartData(String username, String otype) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(otype)) {
            return new ArrayList<>();
        }

        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        
        try {
            String[] columns = {"product", "price"};
            String selection = "username=? AND otype=?";
            String[] selectionArgs = {username, otype};
            c = db.query("cart", columns, selection, selectionArgs, null, null, null);
            
            if (c != null && c.moveToFirst()) {
                do {
                    String product = c.getString(0);
                    String price = c.getString(1);
                    arr.add(product + "$" + price);
                } while (c.moveToNext());
            }
            Log.d("Database", "Cart data retrieval result: " + arr);
        } catch (Exception e) {
            Log.e("Database", "Error getting cart data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        }
        return arr;
    }

    // Add a new appointment to the appointments table
    public boolean addAppointment(String username, String fullname, String address, String contact, String date, String time) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) ||
            TextUtils.isEmpty(address) || TextUtils.isEmpty(contact) ||
            TextUtils.isEmpty(date)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("fullname", fullname);
            cv.put("address", address);
            cv.put("contactno", contact);
            cv.put("date", date);
            cv.put("time", time);
            long result = db.insert("appointments", null, cv);
            Log.d("Database", "Appointment addition result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error adding appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    // --- End AddAppointment ---
    public boolean addOrder(String username, String fullname, String address, String contact, int pincode, String date, String time, float price, String otype) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) || 
            TextUtils.isEmpty(address) || TextUtils.isEmpty(contact) || 
            TextUtils.isEmpty(date) || TextUtils.isEmpty(otype)) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues cv = new ContentValues();
            cv.put("username", username);
            cv.put("fullname", fullname);
            cv.put("address", address);
            cv.put("contactno", contact);
            cv.put("pincode", pincode);
            cv.put("date", date);
            cv.put("time", time);
            cv.put("amount", price);
            cv.put("otype", otype);
            long result = db.insert("orderplace", null, cv);
            Log.d("Database", "Order addition result: " + result);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error adding order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public ArrayList<String> getOrderData(String username) {
        if (TextUtils.isEmpty(username)) {
            return new ArrayList<>();
        }

        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        
        try {
            String[] columns = {"fullname", "address", "contactno", "pincode", "date", "time", "amount", "otype"};
            String selection = "username=?";
            String[] selectionArgs = {username};
            c = db.query("orderplace", columns, selection, selectionArgs, null, null, "date DESC, time DESC");
            
            if (c != null && c.moveToFirst()) {
                do {
                    String orderData = String.format("%s$%s$%s$%s$%s$%s$%.2f$%s",
                        c.getString(0), // fullname
                        c.getString(1), // address
                        c.getString(2), // contact
                        c.getString(3), // pincode
                        c.getString(4), // date
                        c.getString(5), // time
                        c.getFloat(6),  // amount
                        c.getString(7)  // otype
                    );
                    arr.add(orderData);
                } while (c.moveToNext());
            }
            Log.d("Database", "Order data retrieval result: " + arr);
        } catch (Exception e) {
            Log.e("Database", "Error getting order data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        }
        return arr;
    }

    public boolean checkAppointmentExists(String username, String fullname, String address, String contact, String date, String time) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullname) || 
            TextUtils.isEmpty(address) || TextUtils.isEmpty(contact) || 
            TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            return false;
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        try {
            String selection = "username=? AND fullname=? AND address=? AND contactno=? AND date=? AND time=?";
            String[] selectionArgs = {username, fullname, address, contact, date, time};
            c = db.query("appointments", null, selection, selectionArgs, null, null, null);
            boolean result = c != null && c.moveToFirst();
            Log.d("Database", "Appointment existence check result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("Database", "Error checking appointment existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        }
    }

    public boolean deleteAppointment(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            // Delete from 'orderplace' table where otype is 'appointment'
            int rows = db.delete("orderplace", "id=? AND otype=?", new String[]{id, "appointment"});
            return rows > 0;
        } catch (Exception e) {
            Log.e("Database", "Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteOrder(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        try {
            int rows = db.delete("orderplace", "id=?", new String[]{id});
            return rows > 0;
        } catch (Exception e) {
            Log.e("Database", "Error deleting order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Accept an appointment by moving it from 'orderplace' to 'appointments' and deleting from 'orderplace'
    public boolean acceptAppointment(String id) {
        if (TextUtils.isEmpty(id)) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        try {
            // Get the appointment details from orderplace
            c = db.query("orderplace", null, "id=? AND otype=?", new String[]{id, "appointment"}, null, null, null);
            if (c != null && c.moveToFirst()) {
                ContentValues cv = new ContentValues();
                cv.put("username", c.getString(c.getColumnIndex("username")));
                cv.put("fullname", c.getString(c.getColumnIndex("fullname")));
                cv.put("address", c.getString(c.getColumnIndex("address")));
                cv.put("contactno", c.getString(c.getColumnIndex("contactno")));
                cv.put("date", c.getString(c.getColumnIndex("date")));
                cv.put("time", c.getString(c.getColumnIndex("time")));
                // Insert into appointments
                long result = db.insert("appointments", null, cv);
                if (result != -1) {
                    // Delete from orderplace
                    db.delete("orderplace", "id=?", new String[]{id});
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.e("Database", "Error accepting appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            db.close();
        }
    }
}
