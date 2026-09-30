package com.example.fittrack;

import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthlyProgressActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton previousMonthButton;
    private ImageButton nextMonthButton;

    private TextView monthNameText;

    private TextView monthlyStepsText;
    private TextView monthlyCaloriesText;
    private TextView monthlyWorkoutText;
    private TextView monthlyActivityCountText;

    private TextView monthlyStepsGoalText;
    private TextView monthlyCaloriesGoalText;
    private TextView monthlyWorkoutGoalText;

    private TextView monthlyProgressText;
    private TextView monthlyMessageText;

    private ProgressBar monthlyStepsProgressBar;
    private ProgressBar monthlyCaloriesProgressBar;
    private ProgressBar monthlyWorkoutProgressBar;

    private LinearLayout monthlyDailyContainer;

    private DatabaseHelper databaseHelper;

    private final Calendar selectedMonth = Calendar.getInstance();

    private final SimpleDateFormat databaseDateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private final SimpleDateFormat monthFormat =
            new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

    private final SimpleDateFormat dayFormat =
            new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_monthly_progress);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        loadMonthlyProgress();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        previousMonthButton =
                findViewById(R.id.previousMonthButton);

        nextMonthButton =
                findViewById(R.id.nextMonthButton);

        monthNameText =
                findViewById(R.id.monthNameText);

        monthlyStepsText =
                findViewById(R.id.monthlyStepsText);

        monthlyCaloriesText =
                findViewById(R.id.monthlyCaloriesText);

        monthlyWorkoutText =
                findViewById(R.id.monthlyWorkoutText);

        monthlyActivityCountText =
                findViewById(R.id.monthlyActivityCountText);

        monthlyStepsGoalText =
                findViewById(R.id.monthlyStepsGoalText);

        monthlyCaloriesGoalText =
                findViewById(R.id.monthlyCaloriesGoalText);

        monthlyWorkoutGoalText =
                findViewById(R.id.monthlyWorkoutGoalText);

        monthlyProgressText =
                findViewById(R.id.monthlyProgressText);

        monthlyMessageText =
                findViewById(R.id.monthlyMessageText);

        monthlyStepsProgressBar =
                findViewById(R.id.monthlyStepsProgressBar);

        monthlyCaloriesProgressBar =
                findViewById(R.id.monthlyCaloriesProgressBar);

        monthlyWorkoutProgressBar =
                findViewById(R.id.monthlyWorkoutProgressBar);

        monthlyDailyContainer =
                findViewById(R.id.monthlyDailyContainer);
    }

    private void setupButtons() {

        if (backButton != null) {

            backButton.setOnClickListener(v -> finish());
        }

        if (previousMonthButton != null) {

            previousMonthButton.setOnClickListener(v -> {

                selectedMonth.add(Calendar.MONTH, -1);

                loadMonthlyProgress();
            });
        }

        if (nextMonthButton != null) {

            nextMonthButton.setOnClickListener(v -> {

                selectedMonth.add(Calendar.MONTH, 1);

                loadMonthlyProgress();
            });
        }
    }

    private void loadMonthlyProgress() {

        int selectedYear =
                selectedMonth.get(Calendar.YEAR);

        int selectedMonthValue =
                selectedMonth.get(Calendar.MONTH);

        monthNameText.setText(
                monthFormat.format(selectedMonth.getTime())
        );

        Calendar monthCalendar =
                (Calendar) selectedMonth.clone();

        int daysInMonth =
                monthCalendar.getActualMaximum(
                        Calendar.DAY_OF_MONTH
                );

        Map<String, DailyData> dailyData =
                new HashMap<>();

        for (int day = 1; day <= daysInMonth; day++) {

            Calendar dayCalendar =
                    (Calendar) selectedMonth.clone();

            dayCalendar.set(
                    selectedYear,
                    selectedMonthValue,
                    day,
                    0,
                    0,
                    0
            );

            dayCalendar.set(
                    Calendar.MILLISECOND,
                    0
            );

            String date =
                    databaseDateFormat.format(
                            dayCalendar.getTime()
                    );

            dailyData.put(date, new DailyData());
        }

        int totalSteps = 0;
        int totalCalories = 0;
        int totalWorkout = 0;
        int activityCount = 0;

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
                        data.activities++;
                    }

                    totalSteps += steps;
                    totalCalories += calories;
                    totalWorkout += workout;
                    activityCount++;
                }

            } finally {

                cursor.close();
            }
        }

        updateMonthlySummary(
                totalSteps,
                totalCalories,
                totalWorkout,
                activityCount,
                daysInMonth
        );

        updateDailyActivity(
                dailyData,
                selectedYear,
                selectedMonthValue,
                daysInMonth
        );
    }

    private void updateMonthlySummary(
            int totalSteps,
            int totalCalories,
            int totalWorkout,
            int activityCount,
            int daysInMonth) {

        monthlyStepsText.setText(
                String.valueOf(totalSteps)
        );

        monthlyCaloriesText.setText(
                String.valueOf(totalCalories)
        );

        monthlyWorkoutText.setText(
                totalWorkout + " min"
        );

        monthlyActivityCountText.setText(
                String.valueOf(activityCount)
        );

        int dailyStepGoal =
                databaseHelper.getDailyStepGoal();

        int dailyCalorieGoal =
                databaseHelper.getDailyCalorieGoal();

        int dailyWorkoutGoal =
                databaseHelper.getDailyWorkoutGoal();

        int monthlyStepGoal =
                dailyStepGoal * daysInMonth;

        int monthlyCalorieGoal =
                dailyCalorieGoal * daysInMonth;

        int monthlyWorkoutGoal =
                dailyWorkoutGoal * daysInMonth;

        monthlyStepsGoalText.setText(
                "Goal: " + monthlyStepGoal + " steps"
        );

        monthlyCaloriesGoalText.setText(
                "Goal: " + monthlyCalorieGoal + " kcal"
        );

        monthlyWorkoutGoalText.setText(
                "Goal: " + monthlyWorkoutGoal + " min"
        );

        int stepsPercentage =
                calculatePercentage(
                        totalSteps,
                        monthlyStepGoal
                );

        int caloriesPercentage =
                calculatePercentage(
                        totalCalories,
                        monthlyCalorieGoal
                );

        int workoutPercentage =
                calculatePercentage(
                        totalWorkout,
                        monthlyWorkoutGoal
                );

        monthlyStepsProgressBar.setMax(100);

        monthlyCaloriesProgressBar.setMax(100);

        monthlyWorkoutProgressBar.setMax(100);

        monthlyStepsProgressBar.setProgress(
                stepsPercentage
        );

        monthlyCaloriesProgressBar.setProgress(
                caloriesPercentage
        );

        monthlyWorkoutProgressBar.setProgress(
                workoutPercentage
        );

        int overallPercentage =
                (
                        stepsPercentage
                                + caloriesPercentage
                                + workoutPercentage
                ) / 3;

        monthlyProgressText.setText(
                overallPercentage + "% Completed"
        );

        monthlyMessageText.setText(
                getMonthlyMessage(overallPercentage)
        );
    }

    private int calculatePercentage(
            int value,
            int goal) {

        if (goal <= 0) {
            return 0;
        }

        int percentage =
                (int) (((double) value / goal) * 100);

        if (percentage < 0) {
            percentage = 0;
        }

        return Math.min(percentage, 100);
    }

    private String getMonthlyMessage(
            int percentage) {

        if (percentage >= 100) {

            return "Amazing! You achieved your monthly goals.";

        } else if (percentage >= 75) {

            return "Excellent progress! Keep pushing toward 100%.";

        } else if (percentage >= 50) {

            return "Good progress! Stay consistent throughout the month.";

        } else if (percentage >= 25) {

            return "You're building momentum. Keep going!";

        } else {

            return "Every workout counts. Start building your monthly progress.";
        }
    }

    private void updateDailyActivity(
            Map<String, DailyData> dailyData,
            int year,
            int month,
            int daysInMonth) {

        monthlyDailyContainer.removeAllViews();

        addSectionTitle("Monthly Charts");

        addMonthlyChart(
                "Steps",
                dailyData,
                year,
                month,
                daysInMonth,
                1
        );

        addMonthlyChart(
                "Calories",
                dailyData,
                year,
                month,
                daysInMonth,
                2
        );

        addMonthlyChart(
                "Workout Minutes",
                dailyData,
                year,
                month,
                daysInMonth,
                3
        );

        addSectionTitle("Daily Activity");

        Calendar today =
                Calendar.getInstance();

        for (int day = daysInMonth; day >= 1; day--) {

            Calendar calendar =
                    Calendar.getInstance();

            calendar.set(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0
            );

            calendar.set(
                    Calendar.MILLISECOND,
                    0
            );

            String date =
                    databaseDateFormat.format(
                            calendar.getTime()
                    );

            DailyData data =
                    dailyData.get(date);

            if (data == null) {
                data = new DailyData();
            }

            boolean isFutureDate =
                    calendar.after(today);

            if (isFutureDate) {
                continue;
            }

            addDailyRow(
                    dayFormat.format(calendar.getTime()),
                    data
            );
        }
    }

    private void addSectionTitle(
            String title) {

        TextView titleText =
                new TextView(this);

        titleText.setText(title);

        titleText.setTextSize(18);

        titleText.setTextColor(
                getColor(R.color.text_primary)
        );

        titleText.setTypeface(
                null,
                Typeface.BOLD
        );

        titleText.setPadding(
                0,
                20,
                0,
                12
        );

        monthlyDailyContainer.addView(
                titleText
        );
    }

    private void addMonthlyChart(
            String title,
            Map<String, DailyData> dailyData,
            int year,
            int month,
            int daysInMonth,
            int chartType) {

        TextView chartTitle =
                new TextView(this);

        chartTitle.setText(title);

        chartTitle.setTextSize(15);

        chartTitle.setTextColor(
                getColor(R.color.text_primary)
        );

        chartTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        chartTitle.setPadding(
                0,
                8,
                0,
                8
        );

        monthlyDailyContainer.addView(
                chartTitle
        );

        LinearLayout chart =
                new LinearLayout(this);

        chart.setOrientation(
                LinearLayout.HORIZONTAL
        );

        chart.setGravity(
                Gravity.BOTTOM
        );

        chart.setPadding(
                0,
                8,
                0,
                8
        );

        LinearLayout.LayoutParams chartParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(145)
                );

        chart.setLayoutParams(
                chartParams
        );

        int[] values =
                new int[daysInMonth];

        int maximumValue = 0;

        for (int day = 1; day <= daysInMonth; day++) {

            Calendar calendar =
                    Calendar.getInstance();

            calendar.set(
                    year,
                    month,
                    day,
                    0,
                    0,
                    0
            );

            calendar.set(
                    Calendar.MILLISECOND,
                    0
            );

            String date =
                    databaseDateFormat.format(
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

                } else {

                    value = data.workout;
                }
            }

            values[day - 1] = value;

            if (value > maximumValue) {
                maximumValue = value;
            }
        }

        if (maximumValue <= 0) {
            maximumValue = 1;
        }

        for (int i = 0; i < daysInMonth; i++) {

            LinearLayout column =
                    new LinearLayout(this);

            column.setOrientation(
                    LinearLayout.VERTICAL
            );

            column.setGravity(
                    Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL
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

            View bar =
                    new View(this);

            int barHeight =
                    (int) (
                            (values[i]
                                    / (double) maximumValue)
                                    * dpToPx(90)
                    );

            if (values[i] > 0
                    && barHeight < dpToPx(4)) {

                barHeight = dpToPx(4);
            }

            if (values[i] == 0) {
                barHeight = dpToPx(2);
            }

            GradientDrawable background =
                    new GradientDrawable();

            background.setCornerRadius(
                    dpToPx(3)
            );

            if (chartType == 1) {

                background.setColor(
                        getColor(R.color.steps_green)
                );

            } else if (chartType == 2) {

                background.setColor(
                        getColor(R.color.calories_orange)
                );

            } else {

                background.setColor(
                        getColor(R.color.workout_blue)
                );
            }

            bar.setBackground(
                    background
            );

            column.addView(
                    bar,
                    new LinearLayout.LayoutParams(
                            dpToPx(7),
                            barHeight
                    )
            );

            TextView dayText =
                    new TextView(this);

            dayText.setText(
                    String.valueOf(i + 1)
            );

            dayText.setTextSize(8);

            dayText.setGravity(
                    Gravity.CENTER
            );

            dayText.setTextColor(
                    getColor(R.color.text_secondary)
            );

            column.addView(
                    dayText,
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dpToPx(20)
                    )
            );

            chart.addView(
                    column
            );
        }

        monthlyDailyContainer.addView(
                chart
        );
    }

    private void addDailyRow(
            String date,
            DailyData data) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.VERTICAL
        );

        row.setPadding(
                0,
                14,
                0,
                14
        );

        TextView dateText =
                new TextView(this);

        dateText.setText(date);

        dateText.setTextColor(
                getColor(R.color.text_primary)
        );

        dateText.setTextSize(15);

        dateText.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView detailsText =
                new TextView(this);

        detailsText.setText(
                "Steps: "
                        + data.steps
                        + "    •    Calories: "
                        + data.calories
                        + " kcal"
                        + "    •    Workout: "
                        + data.workout
                        + " min"
        );

        detailsText.setTextColor(
                getColor(R.color.text_secondary)
        );

        detailsText.setTextSize(12);

        detailsText.setGravity(
                Gravity.START
        );

        row.addView(
                dateText
        );

        row.addView(
                detailsText
        );

        monthlyDailyContainer.addView(
                row
        );
    }

    private int dpToPx(
            int dp) {

        return (int) (
                dp * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadMonthlyProgress();
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
        int activities = 0;
    }
}