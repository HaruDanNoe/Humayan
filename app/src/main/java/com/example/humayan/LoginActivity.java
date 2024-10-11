package com.example.humayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // If already logged in, redirect to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (isValidLogin(email, password)) {
                // Login success, set session flag and navigate to MainActivity
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("is_logged_in", true);  // Set the session flag
                editor.putString("logged_in_email", email); // Optionally save the email for further use
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Close the login activity
            } else {
                Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private boolean isValidLogin(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String savedUserData = sharedPreferences.getString("user_data", null);

        if (savedUserData != null) {
            try {
                JSONArray usersArray = new JSONArray(savedUserData);
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    String savedEmail = user.getString("email");
                    String savedPassword = user.getString("password");

                    // Check if both email and password match
                    if (email.equals(savedEmail) && password.equals(savedPassword)) {
                        return true;  // Valid login
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;  // Invalid login
    }
}
