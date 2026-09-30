package com.example.fittrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GoalsActivity extends AppCompatActivity {

    private TextView backButton;
    private TextView stepsGoalPercentageText;
    private TextView caloriesGoalPercentageText;
    private TextView workoutGoalPercentageText;
    private TextView overallGoalPercentageText;
    private TextView goalMessageText;
    private TextView saveGoalsButton;

    private EditText stepsGoalEditText;
    private EditText caloriesGoalEditText;
    private EditText workoutGoalEditText;

    private ProgressBar stepsGoalProgressBar;
    private ProgressBar caloriesGoalProgressBar;
    private ProgressBar workoutGoalProgressBar;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goals);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        loadGoals();
        setupButtons();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        stepsGoalPercentageText =
                findViewById(R.id.stepsGoalPercentageText);

        caloriesGoalPercentageText =
                findViewById(R.id.caloriesGoalPercentageText);

        workoutGoalPercentageText =
                findViewById(R.id.workoutGoalPercentageText);

        overallGoalPercentageText =
                findViewById(R.id.overallGoalPercentageText);

        goalMessageText =
                findViewById(R.id.goalMessageText);

        saveGoalsButton =
                findViewById(R.id.saveGoalsButton);

        stepsGoalEditText =
                findViewById(R.id.stepsGoalEditText);

        caloriesGoalEditText =
                findViewById(R.id.caloriesGoalEditText);

        workoutGoalEditText =
                findViewById(R.id.workoutGoalEditText);

        stepsGoalProgressBar =
                findViewById(R.id.stepsGoalProgressBar);

        caloriesGoalProgressBar =
                findViewById(R.id.caloriesGoalProgressBar);

        workoutGoalProgressBar =
                findViewById(R.id.workoutGoalProgressBar);
    }

    private void loadGoals() {

        int stepsGoal =
                databaseHelper.getDailyStepGoal();

        int caloriesGoal =
                databaseHelper.getDailyCalorieGoal();

        int workoutGoal =
                databaseHelper.getDailyWorkoutGoal();

        stepsGoalEditText.setText(
                String.valueOf(stepsGoal)
        );

        caloriesGoalEditText.setText(
                String.valueOf(caloriesGoal)
        );

        workoutGoalEditText.setText(
                String.valueOf(workoutGoal)
        );

        updateProgress();
    }

    private void setupButtons() {

        backButton.setOnClickListener(
                v -> finish()
        );

        saveGoalsButton.setOnClickListener(
                v -> saveGoals()
        );
    }

    private void saveGoals() {

        String stepsText =
                stepsGoalEditText
                        .getText()
                        .toString()
                        .trim();

        String caloriesText =
                caloriesGoalEditText
                        .getText()
                        .toString()
                        .trim();

        String workoutText =
                workoutGoalEditText
                        .getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(stepsText)) {

            stepsGoalEditText.setError(
                    "Enter steps goal"
            );

            stepsGoalEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(caloriesText)) {

            caloriesGoalEditText.setError(
                    "Enter calories goal"
            );

            caloriesGoalEditText.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(workoutText)) {

            workoutGoalEditText.setError(
                    "Enter workout goal"
            );

            workoutGoalEditText.requestFocus();

            return;
        }

        int stepsGoal;
        int caloriesGoal;
        int workoutGoal;

        try {

            stepsGoal =
                    Integer.parseInt(stepsText);

            caloriesGoal =
                    Integer.parseInt(caloriesText);

            workoutGoal =
                    Integer.parseInt(workoutText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (stepsGoal <= 0) {

            stepsGoalEditText.setError(
                    "Goal must be greater than 0"
            );

            stepsGoalEditText.requestFocus();

            return;
        }

        if (caloriesGoal <= 0) {

            caloriesGoalEditText.setError(
                    "Goal must be greater than 0"
            );

            caloriesGoalEditText.requestFocus();

            return;
        }

        if (workoutGoal <= 0) {

            workoutGoalEditText.setError(
                    "Goal must be greater than 0"
            );

            workoutGoalEditText.requestFocus();

            return;
        }

        databaseHelper.setDailyStepGoal(
                stepsGoal
        );

        databaseHelper.setDailyCalorieGoal(
                caloriesGoal
        );

        databaseHelper.setDailyWorkoutGoal(
                workoutGoal
        );

        updateProgress();

        Toast.makeText(
                this,
                "Goals saved successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void updateProgress() {

        int stepsGoal =
                databaseHelper.getDailyStepGoal();

        int caloriesGoal =
                databaseHelper.getDailyCalorieGoal();

        int workoutGoal =
                databaseHelper.getDailyWorkoutGoal();

        /*
         * DatabaseHelper requires the date when
         * requesting today's statistics.
         */
        String today =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        int todaySteps =
                databaseHelper.getTodaySteps(
                        today
                );

        int todayCalories =
                databaseHelper.getTodayCalories(
                        today
                );

        int todayWorkout =
                databaseHelper.getTodayWorkoutMinutes(
                        today
                );

        int stepsPercentage =
                calculatePercentage(
                        todaySteps,
                        stepsGoal
                );

        int caloriesPercentage =
                calculatePercentage(
                        todayCalories,
                        caloriesGoal
                );

        int workoutPercentage =
                calculatePercentage(
                        todayWorkout,
                        workoutGoal
                );

        int overallPercentage =
                (
                        stepsPercentage
                                + caloriesPercentage
                                + workoutPercentage
                ) / 3;

        stepsGoalPercentageText.setText(
                stepsPercentage + "%"
        );

        caloriesGoalPercentageText.setText(
                caloriesPercentage + "%"
        );

        workoutGoalPercentageText.setText(
                workoutPercentage + "%"
        );

        overallGoalPercentageText.setText(
                overallPercentage + "%"
        );

        stepsGoalProgressBar.setMax(100);

        caloriesGoalProgressBar.setMax(100);

        workoutGoalProgressBar.setMax(100);

        stepsGoalProgressBar.setProgress(
                stepsPercentage
        );

        caloriesGoalProgressBar.setProgress(
                caloriesPercentage
        );

        workoutGoalProgressBar.setProgress(
                workoutPercentage
        );

        goalMessageText.setText(
                getGoalMessage(
                        overallPercentage
                )
        );
    }

    private int calculatePercentage(
            int value,
            int goal) {

        if (goal <= 0) {
            return 0;
        }

        int percentage =
                (int) (
                        ((double) value / goal)
                                * 100
                );

        if (percentage < 0) {
            percentage = 0;
        }

        return Math.min(
                percentage,
                100
        );
    }

    private String getGoalMessage(
            int percentage) {

        if (percentage >= 100) {

            return "Excellent! You completed all your daily goals.";

        } else if (percentage >= 75) {

            return "Almost there! A little more effort can complete your goals.";

        } else if (percentage >= 50) {

            return "Great progress! Keep moving toward your targets.";

        } else if (percentage >= 25) {

            return "Good start! Stay consistent and keep going.";

        } else {

            return "Every step counts. Start working toward your daily goals.";
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {

            updateProgress();
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