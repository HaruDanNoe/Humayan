package com.example.humayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                if (isEmailRegistered(email)) {
                    showAlert("Email is already taken.");
                } else {
                    saveUserData(email, password);
                    finish(); // Return to login screen
                }
            } else {
                showAlert("Please fill all fields");
            }
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private boolean isEmailRegistered(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedUserData = sharedPreferences.getString("user_data", null);

        if (savedUserData != null) {
            try {
                JSONArray usersArray = new JSONArray(savedUserData);
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    if (email.equals(user.getString("email"))) {
                        return true;  // Email already registered
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void saveUserData(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String savedUserData = sharedPreferences.getString("user_data", null);
        JSONArray usersArray = new JSONArray();

        // Load existing users if available
        if (savedUserData != null) {
            try {
                usersArray = new JSONArray(savedUserData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Add new user to the array
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("email", email);
            userJson.put("password", password);
            usersArray.put(userJson);  // Append new user
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Save the updated array back to SharedPreferences
        editor.putString("user_data", usersArray.toString());
        editor.apply();
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
