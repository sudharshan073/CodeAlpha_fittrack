package com.example.fittrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddActivity extends AppCompatActivity {

    private EditText exerciseEditText;
    private EditText dateEditText;
    private EditText durationEditText;
    private EditText stepsEditText;
    private EditText caloriesEditText;

    private TextView saveButton;
    private TextView backButton;

    private DatabaseHelper databaseHelper;

    private int activityId = -1;
    private boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        checkEditMode();
        setupDatePicker();
        setupButtons();
    }

    private void initializeViews() {

        exerciseEditText =
                findViewById(R.id.exerciseEditText);

        dateEditText =
                findViewById(R.id.dateEditText);

        durationEditText =
                findViewById(R.id.durationEditText);

        stepsEditText =
                findViewById(R.id.stepsEditText);

        caloriesEditText =
                findViewById(R.id.caloriesEditText);

        saveButton =
                findViewById(R.id.saveButton);

        backButton =
                findViewById(R.id.backButton);
    }

    private void checkEditMode() {

        Intent intent = getIntent();

        if (intent != null &&
                intent.hasExtra("activity_id")) {

            activityId =
                    intent.getIntExtra(
                            "activity_id",
                            -1
                    );

            if (activityId != -1) {

                editMode = true;

                loadActivity(activityId);

                if (saveButton != null) {
                    saveButton.setText("Update Activity");
                }

            } else {

                setDefaultDate();
            }

        } else {

            setDefaultDate();
        }
    }

    private void loadActivity(int id) {

        android.database.Cursor cursor =
                databaseHelper.getActivityById(id);

        if (cursor == null) {
            return;
        }

        try {

            if (cursor.moveToFirst()) {

                String date =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_DATE
                                )
                        );

                String exercise =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_EXERCISE
                                )
                        );

                int duration =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_DURATION
                                )
                        );

                int steps =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_STEPS
                                )
                        );

                int calories =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_CALORIES
                                )
                        );

                exerciseEditText.setText(exercise);
                dateEditText.setText(date);
                durationEditText.setText(
                        String.valueOf(duration)
                );
                stepsEditText.setText(
                        String.valueOf(steps)
                );
                caloriesEditText.setText(
                        String.valueOf(calories)
                );
            }

        } finally {

            cursor.close();
        }
    }

    private void setDefaultDate() {

        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        if (dateEditText != null) {
            dateEditText.setText(today);
        }
    }

    private void setupDatePicker() {

        if (dateEditText == null) {
            return;
        }

        dateEditText.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            String currentDate =
                    dateEditText.getText()
                            .toString()
                            .trim();

            if (!TextUtils.isEmpty(currentDate)) {

                try {

                    Date date =
                            new SimpleDateFormat(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                            ).parse(currentDate);

                    if (date != null) {

                        calendar.setTime(date);
                    }

                } catch (Exception ignored) {
                }
            }

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            AddActivity.this,
                            (view, year, month, dayOfMonth) -> {

                                String selectedDate =
                                        String.format(
                                                Locale.getDefault(),
                                                "%04d-%02d-%02d",
                                                year,
                                                month + 1,
                                                dayOfMonth
                                        );

                                dateEditText.setText(
                                        selectedDate
                                );
                            },
                            calendar.get(
                                    Calendar.YEAR
                            ),
                            calendar.get(
                                    Calendar.MONTH
                            ),
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            )
                    );

            dialog.show();
        });
    }

    private void setupButtons() {

        if (backButton != null) {

            backButton.setOnClickListener(v -> {

                finish();
            });
        }

        if (saveButton != null) {

            saveButton.setOnClickListener(v -> {

                saveActivity();
            });
        }
    }

    private void saveActivity() {

        String exercise =
                exerciseEditText.getText()
                        .toString()
                        .trim();

        String date =
                dateEditText.getText()
                        .toString()
                        .trim();

        String durationText =
                durationEditText.getText()
                        .toString()
                        .trim();

        String stepsText =
                stepsEditText.getText()
                        .toString()
                        .trim();

        String caloriesText =
                caloriesEditText.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(exercise)) {

            exerciseEditText.setError(
                    "Enter exercise name"
            );

            exerciseEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(date)) {

            dateEditText.setError(
                    "Select a date"
            );

            dateEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(durationText)) {

            durationEditText.setError(
                    "Enter workout duration"
            );

            durationEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(stepsText)) {

            stepsEditText.setError(
                    "Enter steps"
            );

            stepsEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(caloriesText)) {

            caloriesEditText.setError(
                    "Enter calories"
            );

            caloriesEditText.requestFocus();

            return;
        }

        int duration;
        int steps;
        int calories;

        try {

            duration =
                    Integer.parseInt(durationText);

            steps =
                    Integer.parseInt(stepsText);

            calories =
                    Integer.parseInt(caloriesText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (duration < 0) {

            durationEditText.setError(
                    "Duration cannot be negative"
            );

            durationEditText.requestFocus();

            return;
        }

        if (steps < 0) {

            stepsEditText.setError(
                    "Steps cannot be negative"
            );

            stepsEditText.requestFocus();

            return;
        }

        if (calories < 0) {

            caloriesEditText.setError(
                    "Calories cannot be negative"
            );

            caloriesEditText.requestFocus();

            return;
        }

        if (editMode) {

            updateExistingActivity(
                    date,
                    exercise,
                    duration,
                    steps,
                    calories
            );

        } else {

            addNewActivity(
                    date,
                    exercise,
                    duration,
                    steps,
                    calories
            );
        }
    }

    private void addNewActivity(
            String date,
            String exercise,
            int duration,
            int steps,
            int calories) {

        long result =
                databaseHelper.addActivity(
                        date,
                        exercise,
                        duration,
                        steps,
                        calories
                );

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Activity saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to save activity",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void updateExistingActivity(
            String date,
            String exercise,
            int duration,
            int steps,
            int calories) {

        int result =
                databaseHelper.updateActivity(
                        activityId,
                        date,
                        exercise,
                        duration,
                        steps,
                        calories
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Activity updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to update activity",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}