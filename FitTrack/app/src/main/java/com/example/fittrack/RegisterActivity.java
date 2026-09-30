package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button registerButton;
    private TextView loginTextView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        usernameEditText =
                findViewById(R.id.registerUsernameEditText);

        passwordEditText =
                findViewById(R.id.registerPasswordEditText);

        confirmPasswordEditText =
                findViewById(R.id.confirmPasswordEditText);

        registerButton =
                findViewById(R.id.registerButton);

        loginTextView =
                findViewById(R.id.loginTextView);

        databaseHelper =
                new DatabaseHelper(this);

        registerButton.setOnClickListener(v -> registerUser());

        loginTextView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }

    private void registerUser() {

        String username =
                usernameEditText.getText()
                        .toString()
                        .trim();

        String password =
                passwordEditText.getText()
                        .toString();

        String confirmPassword =
                confirmPasswordEditText.getText()
                        .toString();

        if (TextUtils.isEmpty(username)) {

            usernameEditText.setError("Enter username");
            usernameEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {

            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 4) {

            passwordEditText.setError(
                    "Password must contain at least 4 characters"
            );

            passwordEditText.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            confirmPasswordEditText.setError(
                    "Passwords do not match"
            );

            confirmPasswordEditText.requestFocus();
            return;
        }

        if (databaseHelper.userExists(username)) {

            Toast.makeText(
                    this,
                    "Username already exists",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        long result =
                databaseHelper.registerUser(
                        username,
                        password
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Registration successful",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

            intent.putExtra(
                    "registered_username",
                    username
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Registration failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}