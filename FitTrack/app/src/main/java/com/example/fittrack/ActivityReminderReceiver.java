package com.example.fittrack;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class ActivityReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID =
            "fittrack_reminder_channel";

    private static final int REMINDER_REQUEST_CODE = 2001;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(
                intent.getAction())) {

            scheduleNextReminder(context);
            return;
        }

        showNotification(context);

        // Schedule the next day's reminder
        scheduleNextReminder(context);
    }

    // =========================================================
    // SHOW NOTIFICATION
    // =========================================================

    private void showNotification(Context context) {

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        int notificationsEnabled =
                databaseHelper.getIntSetting(
                        "notifications",
                        1
                );

        int reminderEnabled =
                databaseHelper.getIntSetting(
                        "reminder",
                        0
                );

        databaseHelper.close();

        if (notificationsEnabled != 1 ||
                reminderEnabled != 1) {

            return;
        }

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU) {

            if (context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }

        createNotificationChannel(context);

        Intent mainIntent = new Intent(
                context,
                MainActivity.class
        );

        mainIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        3002,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle(
                                "FitTrack Daily Reminder"
                        )
                        .setContentText(
                                "Stay active! Complete your daily fitness goals."
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(
                4001,
                builder.build()
        );
    }

    // =========================================================
    // NOTIFICATION CHANNEL
    // =========================================================

    private void createNotificationChannel(
            Context context) {

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {

            return;
        }

        NotificationChannel channel =
                new NotificationChannel(
                        CHANNEL_ID,
                        "FitTrack Reminders",
                        NotificationManager.IMPORTANCE_DEFAULT
                );

        channel.setDescription(
                "Daily fitness activity reminders"
        );

        NotificationManager notificationManager =
                context.getSystemService(
                        NotificationManager.class
                );

        if (notificationManager != null) {

            notificationManager.createNotificationChannel(
                    channel
            );
        }
    }

    // =========================================================
    // SCHEDULE NEXT EXACT REMINDER
    // =========================================================

    private void scheduleNextReminder(Context context) {

        DatabaseHelper databaseHelper =
                new DatabaseHelper(context);

        int reminderEnabled =
                databaseHelper.getIntSetting(
                        "reminder",
                        0
                );

        String savedTime =
                databaseHelper.getSetting(
                        "reminder_time",
                        "08:00"
                );

        databaseHelper.close();

        if (reminderEnabled != 1) {

            return;
        }

        int hour = 8;
        int minute = 0;

        try {

            String[] parts = savedTime.split(":");

            if (parts.length == 2) {

                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            }

        } catch (Exception ignored) {

            hour = 8;
            minute = 0;
        }

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                hour
        );

        calendar.set(
                Calendar.MINUTE,
                minute
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        if (calendar.getTimeInMillis() <=
                System.currentTimeMillis()) {

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(
                        Context.ALARM_SERVICE
                );

        if (alarmManager == null) {

            return;
        }

        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {

                return;
            }
        }

        Intent reminderIntent = new Intent(
                context,
                ActivityReminderReceiver.class
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        REMINDER_REQUEST_CODE,
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }
}