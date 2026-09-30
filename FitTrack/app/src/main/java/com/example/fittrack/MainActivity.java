package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView greetingText;
    private TextView welcomeText;
    private TextView todayDateText;

    private TextView todayStepsText;
    private TextView todayCaloriesText;
    private TextView todayWorkoutText;

    private TextView goalPercentageText;
    private TextView goalStepsText;
    private TextView motivationText;

    private TextView stepGoalText;
    private TextView calorieGoalText;

    private TextView stepProgressPercentageText;
    private TextView calorieProgressPercentageText;
    private TextView workoutProgressPercentageText;

    private ProgressBar dailyProgressBar;

    private View profileButton;
    private View addActivityButton;
    private View historyButton;
    private View bmiButton;
    private View progressButton;
    private View monthlyProgressButton;
    private View goalsButton;
    private View achievementsButton;
    private View reportsButton;
    private View settingsNavButton;

    private View navHome;
    private View navHistory;
    private View navProgress;
    private View navGoals;
    private View navSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        setupBottomNavigation();

        updateDashboard();
        startDashboardAnimation();
    }

    private void initializeViews() {

        greetingText = findViewById(R.id.greetingText);
        welcomeText = findViewById(R.id.welcomeText);
        todayDateText = findViewById(R.id.todayDateText);

        todayStepsText = findViewById(R.id.todayStepsText);
        todayCaloriesText = findViewById(R.id.todayCaloriesText);
        todayWorkoutText = findViewById(R.id.todayWorkoutText);

        goalPercentageText = findViewById(R.id.goalPercentageText);
        dailyProgressBar = findViewById(R.id.dailyProgressBar);

        goalStepsText = findViewById(R.id.goalStepsText);
        motivationText = findViewById(R.id.motivationText);

        stepGoalText = findViewById(R.id.stepGoalText);
        calorieGoalText = findViewById(R.id.calorieGoalText);

        stepProgressPercentageText =
                findViewById(R.id.stepProgressPercentageText);

        calorieProgressPercentageText =
                findViewById(R.id.calorieProgressPercentageText);

        workoutProgressPercentageText =
                findViewById(R.id.workoutProgressPercentageText);

        profileButton = findViewById(R.id.profileButton);
        addActivityButton = findViewById(R.id.addActivityButton);
        historyButton = findViewById(R.id.historyButton);
        progressButton = findViewById(R.id.progressButton);

        monthlyProgressButton =
                findViewById(R.id.monthlyProgressButton);

        goalsButton = findViewById(R.id.goalsButton);

        bmiButton = findViewById(R.id.bmiButton);

        achievementsButton =
                findViewById(R.id.achievementsButton);

        reportsButton =
                findViewById(R.id.reportsButton);

        settingsNavButton =
                findViewById(R.id.settingsNavButton);

        navHome = findViewById(R.id.navHome);
        navHistory = findViewById(R.id.navHistory);
        navProgress = findViewById(R.id.navProgress);
        navGoals = findViewById(R.id.navGoals);
        navSettings = findViewById(R.id.navSettings);
    }

    private void setupButtons() {

        setupClickAnimation(profileButton, () -> {
            startActivityWithAnimation(ProfileActivity.class);
        });

        setupClickAnimation(addActivityButton, () -> {
            startActivityWithAnimation(AddActivity.class);
        });

        setupClickAnimation(historyButton, () -> {
            startActivityWithAnimation(HistoryActivity.class);
        });

        setupClickAnimation(progressButton, () -> {
            startActivityWithAnimation(ProgressActivity.class);
        });

        setupClickAnimation(monthlyProgressButton, () -> {
            startActivityWithAnimation(
                    MonthlyProgressActivity.class
            );
        });

        setupClickAnimation(goalsButton, () -> {
            startActivityWithAnimation(GoalsActivity.class);
        });

        // BMI Calculator
        if (bmiButton != null) {

            setupClickAnimation(bmiButton, () -> {

                Toast.makeText(
                        MainActivity.this,
                        "Opening BMI Calculator",
                        Toast.LENGTH_SHORT
                ).show();

                startActivityWithAnimation(
                        BMICalculatorActivity.class
                );
            });
        }

        // Achievements
        if (achievementsButton != null) {

            setupClickAnimation(achievementsButton, () -> {

                startActivityWithAnimation(
                        AchievementsActivity.class
                );
            });
        }

        // Fitness Reports
        if (reportsButton != null) {

            setupClickAnimation(reportsButton, () -> {

                startActivityWithAnimation(
                        ReportActivity.class
                );
            });
        }

        // Settings
        setupClickAnimation(settingsNavButton, () -> {
            startActivityWithAnimation(SettingsActivity.class);
        });
    }

    private void setupBottomNavigation() {

        setupClickAnimation(navHome, () -> {
            // Already on Home
        });

        setupClickAnimation(navHistory, () -> {
            startActivityWithAnimation(HistoryActivity.class);
        });

        setupClickAnimation(navProgress, () -> {
            startActivityWithAnimation(ProgressActivity.class);
        });

        setupClickAnimation(navGoals, () -> {
            startActivityWithAnimation(GoalsActivity.class);
        });

        setupClickAnimation(navSettings, () -> {
            startActivityWithAnimation(SettingsActivity.class);
        });
    }

    private void setupClickAnimation(
            View view,
            Runnable action) {

        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> {

            v.animate()
                    .scaleX(0.94f)
                    .scaleY(0.94f)
                    .setDuration(90)
                    .withEndAction(() -> {

                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(110)
                                .withEndAction(action)
                                .start();

                    })
                    .start();
        });
    }

    private void startActivityWithAnimation(
            Class<?> activityClass) {

        Intent intent = new Intent(
                MainActivity.this,
                activityClass
        );

        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }

    private void startDashboardAnimation() {

        animateView(greetingText, 0);
        animateView(welcomeText, 60);
        animateView(todayDateText, 100);

        animateView(todayStepsText, 140);
        animateView(todayCaloriesText, 180);
        animateView(todayWorkoutText, 220);

        animateView(goalPercentageText, 260);

        animateView(stepProgressPercentageText, 300);
        animateView(calorieProgressPercentageText, 340);
        animateView(workoutProgressPercentageText, 380);

        animateView(motivationText, 420);

        animateView(addActivityButton, 460);
        animateView(historyButton, 500);
        animateView(progressButton, 540);
        animateView(monthlyProgressButton, 580);
        animateView(goalsButton, 620);
        animateView(bmiButton, 660);
        animateView(achievementsButton, 700);
        animateView(reportsButton, 740);
        animateView(settingsNavButton, 780);

        animateView(navHome, 820);
        animateView(navHistory, 860);
        animateView(navProgress, 900);
        animateView(navGoals, 940);
        animateView(navSettings, 980);
    }

    private void animateView(
            View view,
            long delay) {

        if (view == null) {
            return;
        }

        Animation animation = AnimationUtils.loadAnimation(
                this,
                R.anim.fade_in
        );

        animation.setDuration(500);
        animation.setStartOffset(delay);

        view.startAnimation(animation);
    }

    private void updateDashboard() {

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        String displayDate = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()
        ).format(new Date());

        todayDateText.setText(displayDate);

        greetingText.setText(getGreeting());

        welcomeText.setText(
                "Let's stay active and healthy today"
        );

        int todaySteps =
                databaseHelper.getTodaySteps(today);

        int todayCalories =
                databaseHelper.getTodayCalories(today);

        int todayWorkout =
                databaseHelper.getTodayWorkoutMinutes(today);

        int stepGoal =
                databaseHelper.getDailyStepGoal();

        int calorieGoal =
                databaseHelper.getDailyCalorieGoal();

        int workoutGoal =
                databaseHelper.getDailyWorkoutGoal();

        todayStepsText.setText(
                todaySteps + " steps"
        );

        todayCaloriesText.setText(
                todayCalories + " kcal"
        );

        todayWorkoutText.setText(
                todayWorkout + " min"
        );

        stepGoalText.setText(
                "Daily goal: " + stepGoal + " steps"
        );

        calorieGoalText.setText(
                "Daily goal: " + calorieGoal + " kcal"
        );

        updateGoalCard(
                todaySteps,
                todayCalories,
                todayWorkout,
                stepGoal,
                calorieGoal,
                workoutGoal
        );
    }

    private void updateGoalCard(
            int steps,
            int calories,
            int workout,
            int stepGoal,
            int calorieGoal,
            int workoutGoal) {

        int stepPercentage =
                calculatePercentage(steps, stepGoal);

        int caloriePercentage =
                calculatePercentage(calories, calorieGoal);

        int workoutPercentage =
                calculatePercentage(workout, workoutGoal);

        stepProgressPercentageText.setText(
                "Steps: " + stepPercentage + "%"
        );

        calorieProgressPercentageText.setText(
                "Calories: " + caloriePercentage + "%"
        );

        workoutProgressPercentageText.setText(
                "Workout: " + workoutPercentage + "%"
        );

        int averagePercentage = (
                stepPercentage
                        + caloriePercentage
                        + workoutPercentage
        ) / 3;

        averagePercentage = Math.max(
                0,
                Math.min(averagePercentage, 100)
        );

        goalPercentageText.setText(
                averagePercentage + "%"
        );

        dailyProgressBar.setMax(100);
        dailyProgressBar.setProgress(averagePercentage);

        goalStepsText.setText(
                steps + " / " + stepGoal + " steps"
        );

        updateMotivation(averagePercentage);
    }

    private void updateMotivation(
            int averagePercentage) {

        if (averagePercentage >= 100) {

            motivationText.setText(
                    "Excellent! You completed your daily goals!"
            );

        } else if (averagePercentage >= 75) {

            motivationText.setText(
                    "Great work! You are almost there!"
            );

        } else if (averagePercentage >= 50) {

            motivationText.setText(
                    "Good progress! Keep going!"
            );

        } else if (averagePercentage > 0) {

            motivationText.setText(
                    "Keep moving! Every step counts!"
            );

        } else {

            motivationText.setText(
                    "Start your activity today!"
            );
        }
    }

    private int calculatePercentage(
            int current,
            int goal) {

        if (goal <= 0) {
            return 0;
        }

        int percentage = (int) (
                ((double) current / goal) * 100
        );

        return Math.max(
                0,
                Math.min(percentage, 100)
        );
    }

    private String getGreeting() {

        int hour = Integer.parseInt(
                new SimpleDateFormat(
                        "HH",
                        Locale.getDefault()
                ).format(new Date())
        );

        if (hour < 12) {

            return "Good Morning";

        } else if (hour < 17) {

            return "Good Afternoon";

        } else {

            return "Good Evening";
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            updateDashboard();
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