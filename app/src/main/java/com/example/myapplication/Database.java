package com.example.myapplication;

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
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_EMAIL = "admin@healthcare.com";
    private static final String DATABASE_NAME = "healthcare";
    private static final int DATABASE_VERSION = 1;

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        onCreate(db); // Ensure tables exist
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
            // Drop existing tables if they exist
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS appointments");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS orderplace");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS cart");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");

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
            return 0;
        }

        int result = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            String[] columns = {"isadmin", "password"};
            String selection = "username=?";
            String[] selectionArgs = {username};
            cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndex("password"));
                if (password.equals(storedPassword)) {
                    result = cursor.getInt(cursor.getColumnIndex("isadmin")) == 1 ? 2 : 1;
                }
            }
            Log.d("Database", "Login result for " + username + ": " + result);
        } catch (Exception e) {
            Log.e("Database", "Error during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public ArrayList<String> getAllAppointments() {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = null;
        
        try {
            c = db.query("appointments", null, null, null, null, null, "date ASC, time ASC");
            
            if (c != null && c.moveToFirst()) {
                int fullnameIndex = c.getColumnIndex("fullname");
                int addressIndex = c.getColumnIndex("address");
                int contactnoIndex = c.getColumnIndex("contactno");
                int usernameIndex = c.getColumnIndex("username");
                int dateIndex = c.getColumnIndex("date");
                int timeIndex = c.getColumnIndex("time");

                do {
                    String appointment = String.format("%s | %s | %s | %s | %s %s",
                        c.getString(fullnameIndex),
                        c.getString(addressIndex),
                        c.getString(contactnoIndex),
                        c.getString(usernameIndex),
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
            c = db.query("orderplace", null, null, null, null, null, "date ASC, time ASC");
            
            if (c != null && c.moveToFirst()) {
                int fullnameIndex = c.getColumnIndex("fullname");
                int addressIndex = c.getColumnIndex("address");
                int contactnoIndex = c.getColumnIndex("contactno");
                int usernameIndex = c.getColumnIndex("username");
                int dateIndex = c.getColumnIndex("date");
                int timeIndex = c.getColumnIndex("time");
                int amountIndex = c.getColumnIndex("amount");
                int otypeIndex = c.getColumnIndex("otype");

                do {
                    String order = String.format("%s | %s | %s | %s | %s %s | ₹%.2f | %s",
                        c.getString(fullnameIndex),
                        c.getString(addressIndex),
                        c.getString(contactnoIndex),
                        c.getString(usernameIndex),
                        c.getString(dateIndex),
                        c.getString(timeIndex),
                        c.getDouble(amountIndex),
                        c.getString(otypeIndex)
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
}
