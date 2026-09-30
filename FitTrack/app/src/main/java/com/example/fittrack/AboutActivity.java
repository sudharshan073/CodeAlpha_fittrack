package com.example.fittrack;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        initializeViews();
        setupButtons();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);
    }

    private void setupButtons() {

        backButton.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(
                this,
                new androidx.activity.OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                }
        );
    }
}