package com.example.fittrack;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private TextView backButton;

    private TextView profileInitialText;
    private TextView profileNameText;
    private TextView profileEmailText;
    private TextView profileStatusText;

    private TextView totalStepsText;
    private TextView totalCaloriesText;
    private TextView totalWorkoutText;
    private TextView activityCountText;

    private TextView bestStepsText;
    private TextView bestCaloriesText;
    private TextView bestWorkoutText;

    private TextView profileBadgeCountText;
    private TextView profileAchievementProgressText;

    private TextView profileGoalText;
    private TextView profileHeightText;
    private TextView profileWeightText;
    private TextView profileBmiText;

    private TextView editProfileButton;
    private TextView viewAchievementsButton;
    private TextView profileHistoryButton;
    private TextView profileSettingsButton;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        loadProfile();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        profileInitialText = findViewById(R.id.profileInitialText);
        profileNameText = findViewById(R.id.profileNameText);
        profileEmailText = findViewById(R.id.profileEmailText);

        profileStatusText = findViewById(R.id.profileStatusText);

        totalStepsText = findViewById(R.id.totalStepsText);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        totalWorkoutText = findViewById(R.id.totalWorkoutText);
        activityCountText = findViewById(R.id.activityCountText);

        bestStepsText = findViewById(R.id.bestStepsText);
        bestCaloriesText = findViewById(R.id.bestCaloriesText);
        bestWorkoutText = findViewById(R.id.bestWorkoutText);

        profileBadgeCountText =
                findViewById(R.id.profileBadgeCountText);

        profileAchievementProgressText =
                findViewById(R.id.profileAchievementProgressText);

        profileGoalText = findViewById(R.id.profileGoalText);
        profileHeightText = findViewById(R.id.profileHeightText);
        profileWeightText = findViewById(R.id.profileWeightText);
        profileBmiText = findViewById(R.id.profileBmiText);

        editProfileButton = findViewById(R.id.editProfileButton);
        viewAchievementsButton =
                findViewById(R.id.viewAchievementsButton);

        profileHistoryButton =
                findViewById(R.id.profileHistoryButton);

        profileSettingsButton =
                findViewById(R.id.profileSettingsButton);
    }

    private void setupButtons() {

        backButton.setOnClickListener(v -> finish());

        editProfileButton.setOnClickListener(
                v -> showEditProfileDialog()
        );

        viewAchievementsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    AchievementsActivity.class
            );

            startActivity(intent);
        });

        profileHistoryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    HistoryActivity.class
            );

            startActivity(intent);
        });

        profileSettingsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadProfile() {

        String name = databaseHelper.getSetting(
                "user_name",
                "Sudharshan"
        );

        String email = databaseHelper.getSetting(
                "user_email",
                ""
        );

        if (name == null || name.trim().isEmpty()) {
            name = "Sudharshan";
        }

        profileNameText.setText(name);

        if (email == null || email.trim().isEmpty()) {
            profileEmailText.setText("Email not set");
        } else {
            profileEmailText.setText(email);
        }

        setProfileInitial(name);

        loadStatistics();
        loadPersonalRecords();
        loadAchievementSummary();
        loadFitnessInformation();
    }

    private void setProfileInitial(String name) {

        String initial = "S";

        if (name != null && !name.trim().isEmpty()) {

            initial = name.trim()
                    .substring(0, 1)
                    .toUpperCase(Locale.getDefault());
        }

        profileInitialText.setText(initial);
    }

    private void loadStatistics() {

        int totalSteps = databaseHelper.getTotalSteps();

        int totalCalories = databaseHelper.getTotalCalories();

        int totalWorkout =
                databaseHelper.getTotalWorkoutMinutes();

        int activityCount =
                databaseHelper.getActivityCount();

        totalStepsText.setText(
                String.valueOf(totalSteps)
        );

        totalCaloriesText.setText(
                totalCalories + " kcal"
        );

        totalWorkoutText.setText(
                totalWorkout + " min"
        );

        activityCountText.setText(
                String.valueOf(activityCount)
        );

        if (profileStatusText != null) {

            if (activityCount > 0) {
                profileStatusText.setText(
                        "● Fitness journey active"
                );
            } else {
                profileStatusText.setText(
                        "● Ready to start your journey"
                );
            }
        }
    }

    private void loadPersonalRecords() {

        int bestSteps = 0;
        int bestCalories = 0;
        int bestWorkout = 0;

        Cursor cursor =
                databaseHelper.getAllActivities();

        if (cursor != null) {

            try {

                while (cursor.moveToNext()) {

                    int steps = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_STEPS
                            )
                    );

                    int calories = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_CALORIES
                            )
                    );

                    int workout = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_DURATION
                            )
                    );

                    if (steps > bestSteps) {
                        bestSteps = steps;
                    }

                    if (calories > bestCalories) {
                        bestCalories = calories;
                    }

                    if (workout > bestWorkout) {
                        bestWorkout = workout;
                    }
                }

            } finally {

                cursor.close();
            }
        }

        bestStepsText.setText(
                "Best steps: " + bestSteps
        );

        bestCaloriesText.setText(
                "Best calories: " + bestCalories + " kcal"
        );

        bestWorkoutText.setText(
                "Longest workout: " + bestWorkout + " min"
        );
    }

    private void loadAchievementSummary() {

        int activityCount =
                databaseHelper.getActivityCount();

        int unlocked = 0;

        if (activityCount >= 1) {
            unlocked++;
        }

        if (hasThousandStepsDay()) {
            unlocked++;
        }

        if (hasSevenDayStreak()) {
            unlocked++;
        }

        profileBadgeCountText.setText(
                "🏆 Badges Unlocked: "
                        + unlocked
                        + " / 3"
        );

        profileAchievementProgressText.setText(
                unlocked == 3
                        ? "All achievements unlocked! Keep going!"
                        : "Keep working towards your next achievement!"
        );
    }

    private boolean hasThousandStepsDay() {

        Cursor cursor = databaseHelper.getReadableDatabase()
                .rawQuery(
                        "SELECT date FROM activities " +
                                "GROUP BY date " +
                                "HAVING SUM(steps) >= 1000",
                        null
                );

        try {

            return cursor != null && cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean hasSevenDayStreak() {

        Cursor cursor = databaseHelper.getReadableDatabase()
                .rawQuery(
                        "SELECT DISTINCT date FROM activities " +
                                "ORDER BY date ASC",
                        null
                );

        if (cursor == null) {
            return false;
        }

        try {

            if (!cursor.moveToFirst()) {
                return false;
            }

            int consecutiveDays = 1;
            String previousDate = cursor.getString(0);

            while (cursor.moveToNext()) {

                String currentDate = cursor.getString(0);

                if (isNextDay(previousDate, currentDate)) {

                    consecutiveDays++;

                    if (consecutiveDays >= 7) {
                        return true;
                    }

                } else {

                    consecutiveDays = 1;
                }

                previousDate = currentDate;
            }

            return false;

        } finally {

            cursor.close();
        }
    }

    private boolean isNextDay(
            String previousDate,
            String currentDate) {

        try {

            java.text.SimpleDateFormat format =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            java.util.Date previous =
                    format.parse(previousDate);

            java.util.Date current =
                    format.parse(currentDate);

            if (previous == null || current == null) {
                return false;
            }

            long difference =
                    current.getTime() - previous.getTime();

            long oneDay =
                    24 * 60 * 60 * 1000L;

            return difference == oneDay;

        } catch (Exception e) {

            return false;
        }
    }

    private void loadFitnessInformation() {

        String goal = databaseHelper.getSetting(
                "fitness_goal",
                "Not set"
        );

        String height = databaseHelper.getSetting(
                "height",
                "Not set"
        );

        String weight = databaseHelper.getSetting(
                "weight",
                "Not set"
        );

        String bmi = databaseHelper.getSetting(
                "bmi",
                "Not calculated"
        );

        profileGoalText.setText(
                "🎯 Fitness Goal: " + goal
        );

        profileHeightText.setText(
                "📏 Height: " + height
        );

        profileWeightText.setText(
                "⚖️ Weight: " + weight
        );

        profileBmiText.setText(
                "📋 BMI: " + bmi
        );
    }

    private void showEditProfileDialog() {

        LinearLayout container =
                new LinearLayout(this);

        container.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = (int) (
                20 * getResources()
                        .getDisplayMetrics()
                        .density
        );

        container.setPadding(
                padding,
                0,
                padding,
                0
        );

        EditText nameEditText =
                new EditText(this);

        nameEditText.setHint("Name");
        nameEditText.setSingleLine(true);

        nameEditText.setInputType(
                InputType.TYPE_CLASS_TEXT
        );

        nameEditText.setText(
                databaseHelper.getSetting(
                        "user_name",
                        "Sudharshan"
                )
        );

        EditText emailEditText =
                new EditText(this);

        emailEditText.setHint("Email");
        emailEditText.setSingleLine(true);

        emailEditText.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );

        emailEditText.setText(
                databaseHelper.getSetting(
                        "user_email",
                        ""
                )
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        nameParams.setMargins(
                0,
                12,
                0,
                8
        );

        nameEditText.setLayoutParams(nameParams);

        LinearLayout.LayoutParams emailParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        emailParams.setMargins(
                0,
                8,
                0,
                12
        );

        emailEditText.setLayoutParams(emailParams);

        container.addView(nameEditText);
        container.addView(emailEditText);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Edit Profile")
                        .setView(container)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String name =
                                nameEditText.getText()
                                        .toString()
                                        .trim();

                        String email =
                                emailEditText.getText()
                                        .toString()
                                        .trim();

                        if (name.isEmpty()) {

                            nameEditText.setError(
                                    "Enter your name"
                            );

                            return;
                        }

                        databaseHelper.setSetting(
                                "user_name",
                                name
                        );

                        databaseHelper.setSetting(
                                "user_email",
                                email
                        );

                        loadProfile();

                        Toast.makeText(
                                ProfileActivity.this,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();
                    });
                }
        );

        dialog.show();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadProfile();
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