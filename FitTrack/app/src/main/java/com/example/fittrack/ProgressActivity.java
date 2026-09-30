package com.example.fittrack;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {

    private TextView backButton;

    private TextView weeklyStepsText;
    private TextView weeklyCaloriesText;
    private TextView weeklyWorkoutText;

    private TextView weeklyStepsGoalText;
    private TextView weeklyCaloriesGoalText;
    private TextView weeklyWorkoutGoalText;

    private TextView weeklyProgressText;
    private TextView weeklyMessageText;

    private TextView weeklyDay1Text;
    private TextView weeklyDay2Text;
    private TextView weeklyDay3Text;
    private TextView weeklyDay4Text;
    private TextView weeklyDay5Text;
    private TextView weeklyDay6Text;
    private TextView weeklyDay7Text;

    private ProgressBar weeklyStepsProgressBar;
    private ProgressBar weeklyCaloriesProgressBar;
    private ProgressBar weeklyWorkoutProgressBar;

    private LinearLayout stepsChart;
    private LinearLayout caloriesChart;
    private LinearLayout workoutChart;

    private DatabaseHelper databaseHelper;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
            );

    private final SimpleDateFormat dayFormat =
            new SimpleDateFormat(
                    "EEE, dd MMM",
                    Locale.getDefault()
            );

    private final SimpleDateFormat shortDayFormat =
            new SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress);

        databaseHelper =
                new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        loadWeeklyProgress();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        weeklyStepsText =
                findViewById(R.id.weeklyStepsText);

        weeklyCaloriesText =
                findViewById(R.id.weeklyCaloriesText);

        weeklyWorkoutText =
                findViewById(R.id.weeklyWorkoutText);

        weeklyStepsGoalText =
                findViewById(R.id.weeklyStepsGoalText);

        weeklyCaloriesGoalText =
                findViewById(R.id.weeklyCaloriesGoalText);

        weeklyWorkoutGoalText =
                findViewById(R.id.weeklyWorkoutGoalText);

        weeklyProgressText =
                findViewById(R.id.weeklyProgressText);

        weeklyMessageText =
                findViewById(R.id.weeklyMessageText);

        weeklyStepsProgressBar =
                findViewById(R.id.weeklyStepsProgressBar);

        weeklyCaloriesProgressBar =
                findViewById(R.id.weeklyCaloriesProgressBar);

        weeklyWorkoutProgressBar =
                findViewById(R.id.weeklyWorkoutProgressBar);

        weeklyDay1Text =
                findViewById(R.id.weeklyDay1Text);

        weeklyDay2Text =
                findViewById(R.id.weeklyDay2Text);

        weeklyDay3Text =
                findViewById(R.id.weeklyDay3Text);

        weeklyDay4Text =
                findViewById(R.id.weeklyDay4Text);

        weeklyDay5Text =
                findViewById(R.id.weeklyDay5Text);

        weeklyDay6Text =
                findViewById(R.id.weeklyDay6Text);

        weeklyDay7Text =
                findViewById(R.id.weeklyDay7Text);

        stepsChart =
                findViewById(R.id.stepsChart);

        caloriesChart =
                findViewById(R.id.caloriesChart);

        workoutChart =
                findViewById(R.id.workoutChart);
    }

    private void setupButtons() {

        if (backButton != null) {

            backButton.setOnClickListener(
                    v -> finish()
            );
        }
    }

    private void loadWeeklyProgress() {

        int totalSteps = 0;
        int totalCalories = 0;
        int totalWorkout = 0;

        Map<String, DailyData> dailyData =
                new HashMap<>();

        Calendar calendar =
                Calendar.getInstance();

        /*
         * Create entries for today
         * and the previous six days.
         */
        for (int i = 0; i < 7; i++) {

            String date =
                    dateFormat.format(
                            calendar.getTime()
                    );

            dailyData.put(
                    date,
                    new DailyData()
            );

            calendar.add(
                    Calendar.DAY_OF_MONTH,
                    -1
            );
        }

        Cursor cursor =
                databaseHelper.getAllActivities();

        if (cursor != null) {

            try {

                while (cursor.moveToNext()) {

                    String date =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_DATE
                                    )
                            );

                    if (!dailyData.containsKey(date)) {
                        continue;
                    }

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

                    int workout =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_DURATION
                                    )
                            );

                    DailyData data =
                            dailyData.get(date);

                    if (data != null) {

                        data.steps += steps;
                        data.calories += calories;
                        data.workout += workout;
                    }

                    totalSteps += steps;
                    totalCalories += calories;
                    totalWorkout += workout;
                }

            } finally {

                cursor.close();
            }
        }

        updateSummary(
                totalSteps,
                totalCalories,
                totalWorkout
        );

        updateDailyActivity(
                dailyData
        );

        updateCharts(
                dailyData
        );
    }

    private void updateSummary(
            int totalSteps,
            int totalCalories,
            int totalWorkout) {

        weeklyStepsText.setText(
                String.valueOf(totalSteps)
        );

        weeklyCaloriesText.setText(
                String.valueOf(totalCalories)
        );

        weeklyWorkoutText.setText(
                totalWorkout + " min"
        );

        int dailyStepGoal =
                databaseHelper.getDailyStepGoal();

        int dailyCalorieGoal =
                databaseHelper.getDailyCalorieGoal();

        int dailyWorkoutGoal =
                databaseHelper.getDailyWorkoutGoal();

        int weeklyStepGoal =
                dailyStepGoal * 7;

        int weeklyCalorieGoal =
                dailyCalorieGoal * 7;

        int weeklyWorkoutGoal =
                dailyWorkoutGoal * 7;

        weeklyStepsGoalText.setText(
                "Goal: " + weeklyStepGoal + " steps"
        );

        weeklyCaloriesGoalText.setText(
                "Goal: " + weeklyCalorieGoal + " kcal"
        );

        weeklyWorkoutGoalText.setText(
                "Goal: " + weeklyWorkoutGoal + " min"
        );

        int stepsPercentage =
                calculatePercentage(
                        totalSteps,
                        weeklyStepGoal
                );

        int caloriesPercentage =
                calculatePercentage(
                        totalCalories,
                        weeklyCalorieGoal
                );

        int workoutPercentage =
                calculatePercentage(
                        totalWorkout,
                        weeklyWorkoutGoal
                );

        weeklyStepsProgressBar.setMax(100);
        weeklyCaloriesProgressBar.setMax(100);
        weeklyWorkoutProgressBar.setMax(100);

        weeklyStepsProgressBar.setProgress(
                stepsPercentage
        );

        weeklyCaloriesProgressBar.setProgress(
                caloriesPercentage
        );

        weeklyWorkoutProgressBar.setProgress(
                workoutPercentage
        );

        int overallPercentage =
                (
                        stepsPercentage
                                + caloriesPercentage
                                + workoutPercentage
                ) / 3;

        weeklyProgressText.setText(
                overallPercentage + "%"
        );

        weeklyMessageText.setText(
                getWeeklyMessage(
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

    private String getWeeklyMessage(
            int percentage) {

        if (percentage >= 100) {

            return "Excellent! You achieved your weekly goals.";

        } else if (percentage >= 75) {

            return "Great work! You are very close to your goals.";

        } else if (percentage >= 50) {

            return "Good progress! Keep building your routine.";

        } else if (percentage >= 25) {

            return "You're getting started. Keep moving forward.";

        } else {

            return "Start small today and build your momentum.";
        }
    }

    private void updateDailyActivity(
            Map<String, DailyData> dailyData) {

        Calendar calendar =
                Calendar.getInstance();

        TextView[] dayViews = {
                weeklyDay1Text,
                weeklyDay2Text,
                weeklyDay3Text,
                weeklyDay4Text,
                weeklyDay5Text,
                weeklyDay6Text,
                weeklyDay7Text
        };

        for (int i = 0; i < 7; i++) {

            String date =
                    dateFormat.format(
                            calendar.getTime()
                    );

            DailyData data =
                    dailyData.get(date);

            if (data == null) {
                data = new DailyData();
            }

            String displayDate =
                    dayFormat.format(
                            calendar.getTime()
                    );

            String text =
                    displayDate
                            + "\n"
                            + "Steps: "
                            + data.steps
                            + "    •    Calories: "
                            + data.calories
                            + " kcal"
                            + "    •    Workout: "
                            + data.workout
                            + " min";

            dayViews[i].setText(text);

            calendar.add(
                    Calendar.DAY_OF_MONTH,
                    -1
            );
        }
    }

    private void updateCharts(
            Map<String, DailyData> dailyData) {

        createChart(
                stepsChart,
                dailyData,
                1
        );

        createChart(
                caloriesChart,
                dailyData,
                2
        );

        createChart(
                workoutChart,
                dailyData,
                3
        );
    }

    private void createChart(
            LinearLayout chartLayout,
            Map<String, DailyData> dailyData,
            int chartType) {

        if (chartLayout == null) {
            return;
        }

        chartLayout.removeAllViews();

        int[] values = new int[7];
        String[] labels = new String[7];

        Calendar calendar =
                Calendar.getInstance();

        int maximumValue = 0;

        /*
         * Display oldest day first
         * and today last.
         */
        for (int i = 6; i >= 0; i--) {

            String date =
                    dateFormat.format(
                            calendar.getTime()
                    );

            DailyData data =
                    dailyData.get(date);

            int value = 0;

            if (data != null) {

                if (chartType == 1) {
                    value = data.steps;

                } else if (chartType == 2) {
                    value = data.calories;

                } else if (chartType == 3) {
                    value = data.workout;
                }
            }

            int index = 6 - i;

            values[index] = value;

            labels[index] =
                    shortDayFormat.format(
                            calendar.getTime()
                    );

            if (value > maximumValue) {
                maximumValue = value;
            }

            calendar.add(
                    Calendar.DAY_OF_MONTH,
                    -1
            );
        }

        if (maximumValue <= 0) {
            maximumValue = 1;
        }

        for (int i = 0; i < 7; i++) {

            LinearLayout column =
                    new LinearLayout(this);

            column.setOrientation(
                    LinearLayout.VERTICAL
            );

            column.setGravity(
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
            );

            LinearLayout.LayoutParams columnParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1
                    );

            column.setLayoutParams(
                    columnParams
            );

            TextView valueText =
                    new TextView(this);

            valueText.setText(
                    String.valueOf(values[i])
            );

            valueText.setTextSize(9);
            valueText.setTextColor(
                    getColorCompat(
                            R.color.text_secondary
                    )
            );

            valueText.setGravity(
                    Gravity.CENTER
            );

            column.addView(
                    valueText,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(20)
                    )
            );

            View bar =
                    new View(this);

            int barHeight =
                    (int) (
                            (values[i]
                                    / (double) maximumValue)
                                    * dpToPx(85)
                    );

            if (values[i] > 0 && barHeight < dpToPx(8)) {
                barHeight = dpToPx(8);
            }

            if (values[i] == 0) {
                barHeight = dpToPx(3);
            }

            GradientDrawable barBackground =
                    new GradientDrawable();

            barBackground.setCornerRadius(
                    dpToPx(5)
            );

            if (chartType == 1) {

                barBackground.setColor(
                        getColorCompat(
                                R.color.steps_green
                        )
                );

            } else if (chartType == 2) {

                barBackground.setColor(
                        getColorCompat(
                                R.color.calories_orange
                        )
                );

            } else {

                barBackground.setColor(
                        getColorCompat(
                                R.color.workout_blue
                        )
                );
            }

            bar.setBackground(
                    barBackground
            );

            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(
                            dpToPx(18),
                            barHeight
                    );

            barParams.gravity =
                    Gravity.CENTER_HORIZONTAL;

            column.addView(
                    bar,
                    barParams
            );

            TextView dayText =
                    new TextView(this);

            dayText.setText(
                    labels[i]
            );

            dayText.setTextSize(10);
            dayText.setTextColor(
                    getColorCompat(
                            R.color.text_secondary
                    )
            );

            dayText.setGravity(
                    Gravity.CENTER
            );

            column.addView(
                    dayText,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(22)
                    )
            );

            chartLayout.addView(
                    column
            );
        }
    }

    private int dpToPx(int dp) {

        return (int) (
                dp * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private int getColorCompat(int colorId) {

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.M) {

            return getColor(colorId);

        } else {

            return getResources()
                    .getColor(colorId);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadWeeklyProgress();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {

            databaseHelper.close();
            databaseHelper = null;
        }
    }

    private static class DailyData {

        int steps = 0;
        int calories = 0;
        int workout = 0;
    }
}