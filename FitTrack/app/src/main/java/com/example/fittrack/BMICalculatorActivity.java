package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BMICalculatorActivity extends AppCompatActivity {

    private EditText weightEditText;
    private EditText heightEditText;

    private TextView bmiResultText;
    private TextView bmiCategoryText;
    private TextView bmiMessageText;

    private View backButton;
    private View calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bmi_calculator);

        initializeViews();
        setupButtons();
    }

    private void initializeViews() {

        weightEditText = findViewById(
                R.id.weightEditText
        );

        heightEditText = findViewById(
                R.id.heightEditText
        );

        bmiResultText = findViewById(
                R.id.bmiResultText
        );

        bmiCategoryText = findViewById(
                R.id.bmiCategoryText
        );

        bmiMessageText = findViewById(
                R.id.bmiMessageText
        );

        backButton = findViewById(
                R.id.backButton
        );

        calculateButton = findViewById(
                R.id.calculateButton
        );
    }

    private void setupButtons() {

        backButton.setOnClickListener(v -> {
            finish();
        });

        calculateButton.setOnClickListener(v -> {
            calculateBMI();
        });
    }

    private void calculateBMI() {

        String weightInput =
                weightEditText.getText().toString().trim();

        String heightInput =
                heightEditText.getText().toString().trim();

        if (weightInput.isEmpty()
                || heightInput.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter weight and height",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double weight;
        double heightInCm;

        try {

            weight = Double.parseDouble(weightInput);
            heightInCm = Double.parseDouble(heightInput);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (weight <= 0 || heightInCm <= 0) {

            Toast.makeText(
                    this,
                    "Weight and height must be positive",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (heightInCm < 50 || heightInCm > 250) {

            Toast.makeText(
                    this,
                    "Please enter a valid height",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (weight < 10 || weight > 400) {

            Toast.makeText(
                    this,
                    "Please enter a valid weight",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double heightInMeters =
                heightInCm / 100.0;

        double bmi =
                weight / (heightInMeters * heightInMeters);

        String formattedBMI =
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        bmi
                );

        bmiResultText.setText(formattedBMI);

        displayBMICategory(bmi);
    }

    private void displayBMICategory(double bmi) {

        String category;
        String message;

        if (bmi < 18.5) {

            category = "Underweight";

            message =
                    "Your BMI is below the standard adult range. "
                            + "Consider discussing your nutritional needs "
                            + "with a healthcare professional.";

        } else if (bmi < 25.0) {

            category = "Normal weight";

            message =
                    "Your BMI is within the standard adult range. "
                            + "Continue maintaining balanced nutrition "
                            + "and regular physical activity.";

        } else if (bmi < 30.0) {

            category = "Overweight";

            message =
                    "Your BMI is above the standard adult range. "
                            + "Consider discussing healthy lifestyle "
                            + "goals with a healthcare professional.";

        } else {

            category = "Obesity";

            message =
                    "Your BMI is in the obesity category. "
                            + "A healthcare professional can help you "
                            + "understand your health needs.";

        }

        bmiCategoryText.setText(category);
        bmiMessageText.setText(message);
    }
}