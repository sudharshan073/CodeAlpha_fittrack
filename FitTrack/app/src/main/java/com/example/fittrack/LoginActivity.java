package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    private Button loginButton;
    private TextView registerTextView;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        usernameEditText =
                findViewById(R.id.usernameEditText);

        passwordEditText =
                findViewById(R.id.passwordEditText);

        loginButton =
                findViewById(R.id.loginButton);

        registerTextView =
                findViewById(R.id.registerTextView);

        databaseHelper =
                new DatabaseHelper(this);

        // Display registered username
        Intent receivedIntent = getIntent();

        String registeredUsername =
                receivedIntent.getStringExtra(
                        "registered_username"
                );

        if (registeredUsername != null) {

            usernameEditText.setText(
                    registeredUsername
            );
        }

        // Login button
        loginButton.setOnClickListener(v -> loginUser());

        // Register link
        if (registerTextView != null) {

            registerTextView.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                LoginActivity.this,
                                RegisterActivity.class
                        );

                startActivity(intent);
            });
        }
    }

    private void loginUser() {

        String username =
                usernameEditText.getText()
                        .toString()
                        .trim();

        String password =
                passwordEditText.getText()
                        .toString();

        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean validUser =
                databaseHelper.checkUser(
                        username,
                        password
                );

        if (validUser) {

            Toast.makeText(
                    this,
                    "Login successful",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            MainActivity.class
                    );

            intent.putExtra(
                    "username",
                    username
            );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Invalid username or password",
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