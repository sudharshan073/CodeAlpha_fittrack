package com.example.fittrack;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private static final int REQUEST_BACKUP_FILE = 1001;
    private static final int REQUEST_RESTORE_FILE = 1002;
    private static final int REQUEST_CSV_FILE = 1003;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 3001;
    private static final int REMINDER_REQUEST_CODE = 2001;

    private TextView backButton;
    private Switch darkModeSwitch;
    private Switch notificationsSwitch;
    private Switch reminderSwitch;
    private TextView reminderTimeButton;
    private Spinner unitSpinner;

    private TextView backupButton;
    private TextView restoreButton;
    private TextView activityReportButton;
    private TextView exportCsvButton;
    private TextView aboutButton;
    private TextView privacyPolicyButton;

    // LOGOUT BUTTON
    private TextView logoutButton;

    private DatabaseHelper databaseHelper;

    private boolean changingDarkMode = false;
    private boolean changingNotifications = false;
    private boolean changingReminder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupUnitSpinner();
        loadSettings();
        setupButtons();
    }

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        reminderSwitch = findViewById(R.id.reminderSwitch);

        reminderTimeButton = findViewById(R.id.reminderTimeButton);

        unitSpinner = findViewById(R.id.unitSpinner);

        backupButton = findViewById(R.id.backupButton);
        restoreButton = findViewById(R.id.restoreButton);
        activityReportButton = findViewById(R.id.activityReportButton);
        exportCsvButton = findViewById(R.id.exportCsvButton);

        aboutButton = findViewById(R.id.aboutButton);
        privacyPolicyButton = findViewById(R.id.privacyPolicyButton);

        // LOGOUT BUTTON
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupUnitSpinner() {

        String[] units = {
                "Metric (kg, cm)",
                "Imperial (lb, ft)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitSpinner.setAdapter(adapter);
    }

    private void loadSettings() {

        int darkMode = databaseHelper.getIntSetting(
                "dark_mode",
                0
        );

        int notifications = databaseHelper.getIntSetting(
                "notifications",
                1
        );

        int reminder = databaseHelper.getIntSetting(
                "reminder",
                0
        );

        String reminderTime = databaseHelper.getSetting(
                "reminder_time",
                "08:00"
        );

        String unitSystem = databaseHelper.getSetting(
                "unit_system",
                "metric"
        );

        changingDarkMode = true;
        darkModeSwitch.setChecked(darkMode == 1);
        changingDarkMode = false;

        changingNotifications = true;
        notificationsSwitch.setChecked(notifications == 1);
        changingNotifications = false;

        changingReminder = true;
        reminderSwitch.setChecked(reminder == 1);
        changingReminder = false;

        reminderTimeButton.setText(
                "Reminder time: "
                        + formatReminderTimeForDisplay(reminderTime)
        );

        if ("imperial".equalsIgnoreCase(unitSystem)) {
            unitSpinner.setSelection(1);
        } else {
            unitSpinner.setSelection(0);
        }

        if (reminder == 1) {
            scheduleReminder();
        }
    }

    private void setupButtons() {

        // BACK BUTTON
        backButton.setOnClickListener(v -> finish());

        // DARK MODE
        darkModeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (changingDarkMode) {
                        return;
                    }

                    databaseHelper.setSetting(
                            "dark_mode",
                            isChecked ? "1" : "0"
                    );

                    applyDarkMode(isChecked);
                }
        );

        // NOTIFICATIONS
        notificationsSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (changingNotifications) {
                        return;
                    }

                    if (isChecked) {

                        databaseHelper.setSetting(
                                "notifications",
                                "1"
                        );

                        requestNotificationPermission();

                        Toast.makeText(
                                this,
                                "Notifications enabled",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        databaseHelper.setSetting(
                                "notifications",
                                "0"
                        );

                        Toast.makeText(
                                this,
                                "Notifications disabled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // REMINDER
        reminderSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    if (changingReminder) {
                        return;
                    }

                    databaseHelper.setSetting(
                            "reminder",
                            isChecked ? "1" : "0"
                    );

                    if (isChecked) {

                        requestNotificationPermission();

                        scheduleReminder();

                        Toast.makeText(
                                this,
                                "Daily activity reminder enabled",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        cancelReminder();

                        Toast.makeText(
                                this,
                                "Activity reminder disabled",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // REMINDER TIME
        reminderTimeButton.setOnClickListener(
                v -> showTimePicker()
        );

        // UNIT SYSTEM
        unitSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {

                        if (position == 1) {

                            databaseHelper.setSetting(
                                    "unit_system",
                                    "imperial"
                            );

                        } else {

                            databaseHelper.setSetting(
                                    "unit_system",
                                    "metric"
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        // BACKUP
        backupButton.setOnClickListener(
                v -> startBackup()
        );

        // RESTORE
        restoreButton.setOnClickListener(
                v -> confirmRestore()
        );

        // ACTIVITY REPORT
        activityReportButton.setOnClickListener(
                v -> {

                    Intent intent = new Intent(
                            SettingsActivity.this,
                            ReportActivity.class
                    );

                    startActivity(intent);
                }
        );

        // CSV EXPORT
        exportCsvButton.setOnClickListener(
                v -> startCsvExport()
        );

        // ABOUT
        aboutButton.setOnClickListener(
                v -> {

                    Intent intent = new Intent(
                            SettingsActivity.this,
                            AboutActivity.class
                    );

                    startActivity(intent);
                }
        );

        privacyPolicyButton.setOnClickListener(v -> {

            Toast.makeText(
                    SettingsActivity.this,
                    "Opening Privacy Policy...",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    SettingsActivity.this,
                    PrivacyPolicyActivity.class
            );

            startActivity(intent);
        });
        // LOGOUT
        logoutButton.setOnClickListener(
                v -> showLogoutConfirmation()
        );
    }

    // =========================================================
    // LOGOUT CONFIRMATION
    // =========================================================

    private void showLogoutConfirmation() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage(
                        "Are you sure you want to logout from FitTrack?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Logout",
                        (dialog, which) -> logoutUser()
                )
                .show();
    }

    private void logoutUser() {

        Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
                SettingsActivity.this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

// =========================================================
// NEXT PART
// =========================================================

    // =========================================================
    // NOTIFICATION PERMISSION
    // =========================================================

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    REQUEST_NOTIFICATION_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        this,
                        "Notification permission granted",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Notification permission denied",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // =========================================================
    // REMINDER TIME PICKER
    // =========================================================

    private void showTimePicker() {

        String savedTime = databaseHelper.getSetting(
                "reminder_time",
                "08:00"
        );

        int hour = 8;
        int minute = 0;

        try {

            String[] parts = savedTime.split(":");

            if (parts.length == 2) {

                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            }

        } catch (Exception ignored) {
        }

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {

                    String time24 = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                    );

                    Calendar calendar = Calendar.getInstance();

                    calendar.set(
                            Calendar.HOUR_OF_DAY,
                            selectedHour
                    );

                    calendar.set(
                            Calendar.MINUTE,
                            selectedMinute
                    );

                    calendar.set(
                            Calendar.SECOND,
                            0
                    );

                    SimpleDateFormat displayFormat =
                            new SimpleDateFormat(
                                    "hh:mm a",
                                    Locale.getDefault()
                            );

                    String time12 = displayFormat.format(
                            calendar.getTime()
                    );

                    databaseHelper.setSetting(
                            "reminder_time",
                            time24
                    );

                    reminderTimeButton.setText(
                            "Reminder time: " + time12
                    );

                    int reminder = databaseHelper.getIntSetting(
                            "reminder",
                            0
                    );

                    if (reminder == 1) {

                        cancelReminder();
                        scheduleReminder();
                    }

                    Toast.makeText(
                            this,
                            "Reminder time updated",
                            Toast.LENGTH_SHORT
                    ).show();

                },
                hour,
                minute,
                false
        );

        dialog.show();
    }

    private String formatReminderTimeForDisplay(
            String time24
    ) {

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                    );

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                    );

            return outputFormat.format(
                    inputFormat.parse(time24)
            );

        } catch (Exception e) {

            return time24;
        }
    }

    // =========================================================
    // SCHEDULE DAILY REMINDER
    // =========================================================

    private void scheduleReminder() {

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        String savedTime = databaseHelper.getSetting(
                "reminder_time",
                "08:00"
        );

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

        Calendar calendar = Calendar.getInstance();

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

        if (calendar.getTimeInMillis()
                <= System.currentTimeMillis()) {

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        PendingIntent pendingIntent =
                getReminderPendingIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            if (!alarmManager.canScheduleExactAlarms()) {

                Toast.makeText(
                        this,
                        "Allow exact alarms in phone settings",
                        Toast.LENGTH_LONG
                ).show();

                Intent intent = new Intent(
                        android.provider.Settings
                                .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                );

                startActivity(intent);

                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );

        Toast.makeText(
                this,
                "Reminder scheduled for "
                        + formatReminderTimeForDisplay(savedTime),
                Toast.LENGTH_SHORT
        ).show();
    }

    // =========================================================
    // CANCEL REMINDER
    // =========================================================

    private void cancelReminder() {

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager == null) {
            return;
        }

        PendingIntent pendingIntent =
                getReminderPendingIntent();

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    // =========================================================
    // REMINDER PENDING INTENT
    // =========================================================

    private PendingIntent getReminderPendingIntent() {

        Intent intent = new Intent(
                this,
                ActivityReminderReceiver.class
        );

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        flags |= PendingIntent.FLAG_IMMUTABLE;

        return PendingIntent.getBroadcast(
                this,
                REMINDER_REQUEST_CODE,
                intent,
                flags
        );
    }

    // =========================================================
    // DARK MODE
    // =========================================================

    private void applyDarkMode(boolean enabled) {

        if (enabled) {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );

        } else {

            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

    // =========================================================
    // BACKUP
    // =========================================================

    private void startBackup() {

        Intent intent = new Intent(
                Intent.ACTION_CREATE_DOCUMENT
        );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType(
                "application/octet-stream"
        );

        intent.putExtra(
                Intent.EXTRA_TITLE,
                "FitTrack_Backup.db"
        );

        startActivityForResult(
                intent,
                REQUEST_BACKUP_FILE
        );
    }

    // =========================================================
    // RESTORE
    // =========================================================

    private void confirmRestore() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Restore FitTrack Data"
                )
                .setMessage(
                        "Restoring a backup will replace your current "
                                + "FitTrack data. Continue?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Restore",
                        (dialog, which) -> startRestore()
                )
                .show();
    }

    private void startRestore() {

        Intent intent = new Intent(
                Intent.ACTION_OPEN_DOCUMENT
        );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("*/*");

        startActivityForResult(
                intent,
                REQUEST_RESTORE_FILE
        );
    }

    // =========================================================
    // CSV EXPORT
    // =========================================================

    private void startCsvExport() {

        Intent intent = new Intent(
                Intent.ACTION_CREATE_DOCUMENT
        );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType(
                "text/csv"
        );

        intent.putExtra(
                Intent.EXTRA_TITLE,
                "FitTrack_Activities.csv"
        );

        startActivityForResult(
                intent,
                REQUEST_CSV_FILE
        );
    }

    // =========================================================
    // ACTIVITY RESULT
    // =========================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (resultCode != RESULT_OK
                || data == null
                || data.getData() == null) {

            return;
        }

        Uri uri = data.getData();

        if (requestCode == REQUEST_BACKUP_FILE) {

            performBackup(uri);

        } else if (requestCode == REQUEST_RESTORE_FILE) {

            performRestore(uri);

        } else if (requestCode == REQUEST_CSV_FILE) {

            exportActivitiesToCsv(uri);
        }
    }

    // =========================================================
    // PERFORM BACKUP
    // =========================================================

    private void performBackup(Uri destinationUri) {

        File databaseFile = getDatabasePath(
                "FitTrack.db"
        );

        if (!databaseFile.exists()) {

            Toast.makeText(
                    this,
                    "FitTrack database not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try {

            if (databaseHelper != null) {
                databaseHelper.close();
            }

            InputStream inputStream =
                    new FileInputStream(databaseFile);

            OutputStream outputStream =
                    getContentResolver().openOutputStream(
                            destinationUri
                    );

            if (outputStream == null) {

                inputStream.close();

                databaseHelper =
                        new DatabaseHelper(this);

                Toast.makeText(
                        this,
                        "Unable to create backup file",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            byte[] buffer = new byte[8192];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer))
                    != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            outputStream.flush();

            inputStream.close();
            outputStream.close();

            databaseHelper =
                    new DatabaseHelper(this);

            Toast.makeText(
                    this,
                    "Backup created successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            databaseHelper =
                    new DatabaseHelper(this);

            Toast.makeText(
                    this,
                    "Backup failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // PERFORM RESTORE
    // =========================================================

    private void performRestore(Uri backupUri) {

        File databaseFile = getDatabasePath(
                "FitTrack.db"
        );

        File databaseJournal = new File(
                databaseFile.getPath() + "-journal"
        );

        File databaseWal = new File(
                databaseFile.getPath() + "-wal"
        );

        File databaseShm = new File(
                databaseFile.getPath() + "-shm"
        );

        try {

            if (databaseHelper != null) {

                databaseHelper.close();
                databaseHelper = null;
            }

            InputStream inputStream =
                    getContentResolver().openInputStream(
                            backupUri
                    );

            if (inputStream == null) {

                databaseHelper =
                        new DatabaseHelper(this);

                Toast.makeText(
                        this,
                        "Unable to read backup file",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            OutputStream outputStream =
                    new FileOutputStream(
                            databaseFile,
                            false
                    );

            byte[] buffer = new byte[8192];

            int bytesRead;

            while ((bytesRead = inputStream.read(buffer))
                    != -1) {

                outputStream.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            outputStream.flush();

            inputStream.close();
            outputStream.close();

            if (databaseJournal.exists()) {
                databaseJournal.delete();
            }

            if (databaseWal.exists()) {
                databaseWal.delete();
            }

            if (databaseShm.exists()) {
                databaseShm.delete();
            }

            databaseHelper =
                    new DatabaseHelper(this);

            Toast.makeText(
                    this,
                    "Restore completed successfully",
                    Toast.LENGTH_LONG
            ).show();

            restartApplication();

        } catch (Exception e) {

            databaseHelper =
                    new DatabaseHelper(this);

            Toast.makeText(
                    this,
                    "Restore failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // CSV EXPORT
    // =========================================================

    private void exportActivitiesToCsv(
            Uri destinationUri
    ) {

        Cursor cursor = null;

        try {

            OutputStream outputStream =
                    getContentResolver().openOutputStream(
                            destinationUri
                    );

            if (outputStream == null) {

                Toast.makeText(
                        this,
                        "Unable to create CSV file",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            StringBuilder csv = new StringBuilder();

            csv.append(
                    "ID,Date,Exercise,Duration (min),"
                            + "Steps,Calories (kcal)\n"
            );

            cursor = databaseHelper.getAllActivities();

            int exportedCount = 0;

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_ID
                            )
                    );

                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_DATE
                            )
                    );

                    String exercise = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_EXERCISE
                            )
                    );

                    int duration = cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COLUMN_DURATION
                            )
                    );

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

                    csv.append(id).append(",");

                    csv.append(
                            escapeCsvValue(date)
                    ).append(",");

                    csv.append(
                            escapeCsvValue(exercise)
                    ).append(",");

                    csv.append(duration).append(",");

                    csv.append(steps).append(",");

                    csv.append(calories).append("\n");

                    exportedCount++;
                }
            }

            byte[] data = csv.toString().getBytes(
                    StandardCharsets.UTF_8
            );

            outputStream.write(data);

            outputStream.flush();
            outputStream.close();

            Toast.makeText(
                    this,
                    exportedCount
                            + " activities exported successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "CSV export failed: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String escapeCsvValue(
            String value
    ) {

        if (value == null) {
            return "";
        }

        String escaped = value.replace(
                "\"",
                "\"\""
        );

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {

            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    // =========================================================
    // RESTART APPLICATION
    // =========================================================

    private void restartApplication() {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(
                        getPackageName()
                );

        if (intent != null) {

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        }

        finish();
    }

    // =========================================================
    // RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper == null) {

            databaseHelper =
                    new DatabaseHelper(this);
        }

        loadSettings();
    }

    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {

            databaseHelper.close();
            databaseHelper = null;
        }
    }
}