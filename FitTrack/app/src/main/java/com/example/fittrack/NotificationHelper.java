package com.example.fittrack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static final String CHANNEL_ID =
            "fittrack_activity_reminders";

    private static final String CHANNEL_NAME =
            "Activity Reminders";

    private static final String CHANNEL_DESCRIPTION =
            "Reminders to stay active";

    public static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    );

            channel.setDescription(
                    CHANNEL_DESCRIPTION
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showActivityReminder(Context context) {

        createNotificationChannel(context);

        Intent intent =
                new Intent(context, MainActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        100,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
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
                                "Time to stay active!"
                        )
                        .setContentText(
                                "Take a short break and work toward your FitTrack goals."
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_DEFAULT
                        )
                        .setAutoCancel(true)
                        .setContentIntent(
                                pendingIntent
                        );

        try {

            NotificationManagerCompat.from(context)
                    .notify(
                            1001,
                            builder.build()
                    );

        } catch (SecurityException ignored) {
            // Notification permission may not be granted.
        }
    }
}