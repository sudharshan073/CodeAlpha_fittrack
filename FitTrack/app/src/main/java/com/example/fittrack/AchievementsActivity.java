package com.example.fittrack;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AchievementsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView badgeCountText;
    private TextView overallProgressText;
    private ProgressBar achievementProgressBar;

    private TextView firstWorkoutStatus;
    private TextView thousandStepsStatus;
    private TextView sevenDayStreakStatus;

    private View backButton;

    private int unlockedCount = 0;
    private final int totalBadges = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_achievements);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupBackButton();
        updateAchievements();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        badgeCountText =
                findViewById(R.id.badgeCountText);

        overallProgressText =
                findViewById(R.id.overallProgressText);

        achievementProgressBar =
                findViewById(R.id.achievementProgressBar);

        firstWorkoutStatus =
                findViewById(R.id.firstWorkoutStatus);

        thousandStepsStatus =
                findViewById(R.id.thousandStepsStatus);

        sevenDayStreakStatus =
                findViewById(R.id.sevenDayStreakStatus);
    }

    private void setupBackButton() {

        if (backButton != null) {

            backButton.setOnClickListener(v -> {
                finish();
            });
        }
    }

    private void updateAchievements() {

        unlockedCount = 0;

        boolean firstWorkout =
                databaseHelper.getActivityCount() >= 1;

        boolean thousandSteps =
                hasReachedThousandSteps();

        boolean sevenDayStreak =
                hasSevenDayStreak();

        if (firstWorkout) {

            firstWorkoutStatus.setText("✓ Unlocked");
            firstWorkoutStatus.setTextColor(
                    getColor(android.R.color.holo_green_dark)
            );

            unlockedCount++;

        } else {

            firstWorkoutStatus.setText("🔒 Locked");
        }

        if (thousandSteps) {

            thousandStepsStatus.setText("✓ Unlocked");
            thousandStepsStatus.setTextColor(
                    getColor(android.R.color.holo_green_dark)
            );

            unlockedCount++;

        } else {

            thousandStepsStatus.setText("🔒 Locked");
        }

        if (sevenDayStreak) {

            sevenDayStreakStatus.setText("✓ Unlocked");
            sevenDayStreakStatus.setTextColor(
                    getColor(android.R.color.holo_green_dark)
            );

            unlockedCount++;

        } else {

            sevenDayStreakStatus.setText("🔒 Locked");
        }

        badgeCountText.setText(
                unlockedCount + "/" + totalBadges
        );

        achievementProgressBar.setMax(totalBadges);
        achievementProgressBar.setProgress(unlockedCount);

        if (unlockedCount == totalBadges) {

            overallProgressText.setText(
                    "Excellent! All badges unlocked!"
            );

        } else {

            overallProgressText.setText(
                    "Keep going! Unlock " +
                            (totalBadges - unlockedCount) +
                            " more badge(s)."
            );
        }
    }

    private boolean hasReachedThousandSteps() {

        Cursor cursor = null;

        try {

            cursor = databaseHelper.getReadableDatabase()
                    .rawQuery(
                            "SELECT date FROM activities " +
                                    "GROUP BY date " +
                                    "HAVING SUM(steps) >= 1000",

                            null
                    );

            return cursor.moveToFirst();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean hasSevenDayStreak() {

        Cursor cursor = null;

        Set<String> activityDates =
                new HashSet<>();

        try {

            cursor = databaseHelper.getReadableDatabase()
                    .rawQuery(
                            "SELECT DISTINCT date " +
                                    "FROM activities " +
                                    "ORDER BY date ASC",

                            null
                    );

            while (cursor.moveToNext()) {

                activityDates.add(
                        cursor.getString(0)
                );
            }

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (activityDates.size() < 7) {
            return false;
        }

        ArrayList<String> dates =
                new ArrayList<>(activityDates);

        Collections.sort(dates);

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        int consecutiveDays = 1;

        for (int i = 1; i < dates.size(); i++) {

            try {

                Date previousDate =
                        dateFormat.parse(
                                dates.get(i - 1)
                        );

                Date currentDate =
                        dateFormat.parse(
                                dates.get(i)
                        );

                if (previousDate == null ||
                        currentDate == null) {

                    continue;
                }

                Calendar previousCalendar =
                        Calendar.getInstance();

                Calendar currentCalendar =
                        Calendar.getInstance();

                previousCalendar.setTime(
                        previousDate
                );

                currentCalendar.setTime(
                        currentDate
                );

                long difference =
                        currentCalendar.getTimeInMillis()
                                - previousCalendar.getTimeInMillis();

                long daysDifference =
                        difference /
                                (24 * 60 * 60 * 1000);

                if (daysDifference == 1) {

                    consecutiveDays++;

                    if (consecutiveDays >= 7) {
                        return true;
                    }

                } else {

                    consecutiveDays = 1;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            updateAchievements();
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