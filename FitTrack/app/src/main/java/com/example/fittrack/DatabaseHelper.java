package com.example.fittrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FitTrack.db";
    private static final int DATABASE_VERSION = 5;

    // ============================================================
    // TABLE NAMES
    // ============================================================

    public static final String TABLE_ACTIVITIES = "activities";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_ACHIEVEMENTS = "achievements";
    public static final String TABLE_PERSONAL_RECORDS = "personal_records";
    public static final String TABLE_USERS = "users";

    // ============================================================
    // ACTIVITY COLUMNS
    // ============================================================

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_STEPS = "steps";
    public static final String COLUMN_CALORIES = "calories";

    // ============================================================
    // SETTINGS COLUMNS
    // ============================================================

    public static final String COLUMN_SETTING_KEY = "setting_key";
    public static final String COLUMN_SETTING_VALUE = "setting_value";

    // ============================================================
    // ACHIEVEMENT COLUMNS
    // ============================================================

    public static final String COLUMN_ACHIEVEMENT_ID = "achievement_id";
    public static final String COLUMN_ACHIEVEMENT_NAME = "achievement_name";
    public static final String COLUMN_ACHIEVEMENT_DESCRIPTION = "description";
    public static final String COLUMN_ACHIEVEMENT_UNLOCKED = "unlocked";

    // ============================================================
    // RECORD COLUMNS
    // ============================================================

    public static final String COLUMN_RECORD_ID = "record_id";
    public static final String COLUMN_RECORD_TYPE = "record_type";
    public static final String COLUMN_RECORD_VALUE = "record_value";
    public static final String COLUMN_RECORD_DATE = "record_date";

    // ============================================================
    // USER COLUMNS
    // ============================================================

    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // ============================================================
    // SETTING KEYS
    // ============================================================

    public static final String KEY_STEP_GOAL = "daily_step_goal";
    public static final String KEY_CALORIE_GOAL = "daily_calorie_goal";
    public static final String KEY_WORKOUT_GOAL = "daily_workout_goal";

    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";

    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WEIGHT = "weight";

    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_NOTIFICATIONS = "notifications";
    public static final String KEY_REMINDER = "reminder";
    public static final String KEY_UNIT_SYSTEM = "unit_system";

    // ============================================================
    // DEFAULT VALUES
    // ============================================================

    private static final int DEFAULT_STEP_GOAL = 10000;
    private static final int DEFAULT_CALORIE_GOAL = 500;
    private static final int DEFAULT_WORKOUT_GOAL = 30;

    public DatabaseHelper(Context context) {
        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    // ============================================================
    // DATABASE CREATION
    // ============================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ACTIVITIES TABLE

        db.execSQL(
                "CREATE TABLE " + TABLE_ACTIVITIES + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT NOT NULL, " +
                        COLUMN_EXERCISE + " TEXT NOT NULL, " +
                        COLUMN_DURATION + " INTEGER NOT NULL, " +
                        COLUMN_STEPS + " INTEGER NOT NULL, " +
                        COLUMN_CALORIES + " INTEGER NOT NULL)"
        );

        // SETTINGS TABLE

        db.execSQL(
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        COLUMN_SETTING_KEY + " TEXT PRIMARY KEY, " +
                        COLUMN_SETTING_VALUE + " TEXT NOT NULL)"
        );

        // ACHIEVEMENTS TABLE

        db.execSQL(
                "CREATE TABLE " + TABLE_ACHIEVEMENTS + " (" +
                        COLUMN_ACHIEVEMENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_ACHIEVEMENT_NAME + " TEXT NOT NULL, " +
                        COLUMN_ACHIEVEMENT_DESCRIPTION + " TEXT NOT NULL, " +
                        COLUMN_ACHIEVEMENT_UNLOCKED +
                        " INTEGER NOT NULL DEFAULT 0)"
        );

        // PERSONAL RECORDS TABLE

        db.execSQL(
                "CREATE TABLE " + TABLE_PERSONAL_RECORDS + " (" +
                        COLUMN_RECORD_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_RECORD_TYPE + " TEXT NOT NULL, " +
                        COLUMN_RECORD_VALUE + " INTEGER NOT NULL, " +
                        COLUMN_RECORD_DATE + " TEXT NOT NULL)"
        );

        // USERS TABLE

        db.execSQL(
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COLUMN_USER_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME +
                        " TEXT UNIQUE NOT NULL, " +
                        COLUMN_PASSWORD +
                        " TEXT NOT NULL)"
        );

        // DEFAULT SETTINGS

        insertDefaultSetting(
                db,
                KEY_STEP_GOAL,
                String.valueOf(DEFAULT_STEP_GOAL)
        );

        insertDefaultSetting(
                db,
                KEY_CALORIE_GOAL,
                String.valueOf(DEFAULT_CALORIE_GOAL)
        );

        insertDefaultSetting(
                db,
                KEY_WORKOUT_GOAL,
                String.valueOf(DEFAULT_WORKOUT_GOAL)
        );

        insertDefaultSetting(
                db,
                KEY_USER_NAME,
                "Sudharshan"
        );

        insertDefaultSetting(
                db,
                KEY_USER_EMAIL,
                ""
        );

        insertDefaultSetting(
                db,
                KEY_HEIGHT,
                "0"
        );

        insertDefaultSetting(
                db,
                KEY_WEIGHT,
                "0"
        );

        insertDefaultSetting(
                db,
                KEY_DARK_MODE,
                "0"
        );

        insertDefaultSetting(
                db,
                KEY_NOTIFICATIONS,
                "1"
        );

        insertDefaultSetting(
                db,
                KEY_REMINDER,
                "0"
        );

        insertDefaultSetting(
                db,
                KEY_UNIT_SYSTEM,
                "metric"
        );

        // DEFAULT ACHIEVEMENTS

        insertAchievement(
                db,
                "First Step",
                "Complete your first activity"
        );

        insertAchievement(
                db,
                "Week Warrior",
                "Stay active for 7 days"
        );

        insertAchievement(
                db,
                "Calorie Crusher",
                "Burn 5,000 total calories"
        );

        insertAchievement(
                db,
                "Step Champion",
                "Reach 100,000 total steps"
        );

        insertAchievement(
                db,
                "Consistency",
                "Complete 300 workout minutes"
        );

        insertAchievement(
                db,
                "Fitness Pro",
                "Complete 30 activities"
        );

        insertAchievement(
                db,
                "Goal Getter",
                "Complete a daily goal"
        );
    }

    // ============================================================
    // DATABASE UPGRADE
    // ============================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_SETTINGS +
                            " (" +
                            COLUMN_SETTING_KEY +
                            " TEXT PRIMARY KEY, " +
                            COLUMN_SETTING_VALUE +
                            " TEXT NOT NULL)"
            );

            insertDefaultSetting(
                    db,
                    KEY_STEP_GOAL,
                    String.valueOf(DEFAULT_STEP_GOAL)
            );
        }

        if (oldVersion < 3) {

            insertDefaultSetting(
                    db,
                    KEY_CALORIE_GOAL,
                    String.valueOf(DEFAULT_CALORIE_GOAL)
            );

            insertDefaultSetting(
                    db,
                    KEY_WORKOUT_GOAL,
                    String.valueOf(DEFAULT_WORKOUT_GOAL)
            );
        }

        if (oldVersion < 4) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_ACHIEVEMENTS +
                            " (" +
                            COLUMN_ACHIEVEMENT_ID +
                            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_ACHIEVEMENT_NAME +
                            " TEXT NOT NULL, " +
                            COLUMN_ACHIEVEMENT_DESCRIPTION +
                            " TEXT NOT NULL, " +
                            COLUMN_ACHIEVEMENT_UNLOCKED +
                            " INTEGER NOT NULL DEFAULT 0)"
            );

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_PERSONAL_RECORDS +
                            " (" +
                            COLUMN_RECORD_ID +
                            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_RECORD_TYPE +
                            " TEXT NOT NULL, " +
                            COLUMN_RECORD_VALUE +
                            " INTEGER NOT NULL, " +
                            COLUMN_RECORD_DATE +
                            " TEXT NOT NULL)"
            );

            insertDefaultSetting(
                    db,
                    KEY_USER_NAME,
                    "Sudharshan"
            );

            insertDefaultSetting(
                    db,
                    KEY_USER_EMAIL,
                    ""
            );

            insertDefaultSetting(
                    db,
                    KEY_HEIGHT,
                    "0"
            );

            insertDefaultSetting(
                    db,
                    KEY_WEIGHT,
                    "0"
            );

            insertDefaultSetting(
                    db,
                    KEY_DARK_MODE,
                    "0"
            );

            insertDefaultSetting(
                    db,
                    KEY_NOTIFICATIONS,
                    "1"
            );

            insertDefaultSetting(
                    db,
                    KEY_REMINDER,
                    "0"
            );

            insertDefaultSetting(
                    db,
                    KEY_UNIT_SYSTEM,
                    "metric"
            );

            insertAchievement(
                    db,
                    "First Step",
                    "Complete your first activity"
            );

            insertAchievement(
                    db,
                    "Week Warrior",
                    "Stay active for 7 days"
            );

            insertAchievement(
                    db,
                    "Calorie Crusher",
                    "Burn 5,000 total calories"
            );

            insertAchievement(
                    db,
                    "Step Champion",
                    "Reach 100,000 total steps"
            );

            insertAchievement(
                    db,
                    "Consistency",
                    "Complete 300 workout minutes"
            );

            insertAchievement(
                    db,
                    "Fitness Pro",
                    "Complete 30 activities"
            );

            insertAchievement(
                    db,
                    "Goal Getter",
                    "Complete a daily goal"
            );
        }

        if (oldVersion < 5) {

            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " +
                            TABLE_USERS +
                            " (" +
                            COLUMN_USER_ID +
                            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_USERNAME +
                            " TEXT UNIQUE NOT NULL, " +
                            COLUMN_PASSWORD +
                            " TEXT NOT NULL)"
            );
        }
    }

    // ============================================================
    // DEFAULT SETTING
    // ============================================================

    private void insertDefaultSetting(
            SQLiteDatabase db,
            String key,
            String value) {

        ContentValues values = new ContentValues();

        values.put(
                COLUMN_SETTING_KEY,
                key
        );

        values.put(
                COLUMN_SETTING_VALUE,
                value
        );

        db.insertWithOnConflict(
                TABLE_SETTINGS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    // ============================================================
    // ACHIEVEMENT INSERT
    // ============================================================

    private void insertAchievement(
            SQLiteDatabase db,
            String name,
            String description) {

        ContentValues values = new ContentValues();

        values.put(
                COLUMN_ACHIEVEMENT_NAME,
                name
        );

        values.put(
                COLUMN_ACHIEVEMENT_DESCRIPTION,
                description
        );

        values.put(
                COLUMN_ACHIEVEMENT_UNLOCKED,
                0
        );

        db.insert(
                TABLE_ACHIEVEMENTS,
                null,
                values
        );
    }

    // ============================================================
    // USER FUNCTIONS
    // ============================================================

    public long registerUser(
            String username,
            String password) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_USERNAME,
                username
        );

        values.put(
                COLUMN_PASSWORD,
                password
        );

        return db.insert(
                TABLE_USERS,
                null,
                values
        );
    }

    public boolean checkUser(
            String username,
            String password) {

        Cursor cursor =
                getReadableDatabase().query(
                        TABLE_USERS,
                        new String[]{
                                COLUMN_USER_ID
                        },
                        COLUMN_USERNAME + "=? AND " +
                                COLUMN_PASSWORD + "=?",
                        new String[]{
                                username,
                                password
                        },
                        null,
                        null,
                        null
                );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    public boolean userExists(String username) {

        Cursor cursor =
                getReadableDatabase().query(
                        TABLE_USERS,
                        new String[]{
                                COLUMN_USER_ID
                        },
                        COLUMN_USERNAME + "=?",
                        new String[]{
                                username
                        },
                        null,
                        null,
                        null
                );

        boolean exists =
                cursor.moveToFirst();

        cursor.close();

        return exists;
    }

    // ============================================================
    // ACTIVITY FUNCTIONS
    // ============================================================

    public long addActivity(
            String date,
            String exercise,
            int duration,
            int steps,
            int calories) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_DATE, date);
        values.put(COLUMN_EXERCISE, exercise);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_STEPS, steps);
        values.put(COLUMN_CALORIES, calories);

        return db.insert(
                TABLE_ACTIVITIES,
                null,
                values
        );
    }

    public int updateActivity(
            int id,
            String date,
            String exercise,
            int duration,
            int steps,
            int calories) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_DATE, date);
        values.put(COLUMN_EXERCISE, exercise);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_STEPS, steps);
        values.put(COLUMN_CALORIES, calories);

        return db.update(
                TABLE_ACTIVITIES,
                values,
                COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public int deleteActivity(int id) {

        return getWritableDatabase().delete(
                TABLE_ACTIVITIES,
                COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    public Cursor getAllActivities() {

        return getReadableDatabase().query(
                TABLE_ACTIVITIES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC"
        );
    }

    public Cursor getActivityById(int id) {

        return getReadableDatabase().query(
                TABLE_ACTIVITIES,
                null,
                COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );
    }

    // ============================================================
    // TODAY STATISTICS
    // ============================================================

    private int getDateTotal(
            String column,
            String date) {

        Cursor cursor =
                getReadableDatabase().rawQuery(
                        "SELECT COALESCE(SUM(" +
                                column +
                                "),0) FROM " +
                                TABLE_ACTIVITIES +
                                " WHERE " +
                                COLUMN_DATE +
                                "=?",
                        new String[]{
                                date
                        }
                );

        int result = 0;

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();

        return result;
    }

    public int getTodaySteps(String date) {

        return getDateTotal(
                COLUMN_STEPS,
                date
        );
    }

    public int getTodayCalories(String date) {

        return getDateTotal(
                COLUMN_CALORIES,
                date
        );
    }

    public int getTodayWorkoutMinutes(String date) {

        return getDateTotal(
                COLUMN_DURATION,
                date
        );
    }

    // ============================================================
    // TOTAL STATISTICS
    // ============================================================

    public int getTotalSteps() {

        return getTotal(COLUMN_STEPS);
    }

    public int getTotalCalories() {

        return getTotal(COLUMN_CALORIES);
    }

    public int getTotalWorkoutMinutes() {

        return getTotal(COLUMN_DURATION);
    }

    private int getTotal(String column) {

        Cursor cursor =
                getReadableDatabase().rawQuery(
                        "SELECT COALESCE(SUM(" +
                                column +
                                "),0) FROM " +
                                TABLE_ACTIVITIES,
                        null
                );

        int result = 0;

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();

        return result;
    }

    public int getActivityCount() {

        Cursor cursor =
                getReadableDatabase().rawQuery(
                        "SELECT COUNT(*) FROM " +
                                TABLE_ACTIVITIES,
                        null
                );

        int result = 0;

        if (cursor.moveToFirst()) {
            result = cursor.getInt(0);
        }

        cursor.close();

        return result;
    }

    // ============================================================
    // DAILY / WEEKLY DATA
    // ============================================================

    public Cursor getLast7Days() {

        return getReadableDatabase().rawQuery(
                "SELECT " +
                        COLUMN_DATE +
                        ", SUM(" +
                        COLUMN_STEPS +
                        ") AS steps, SUM(" +
                        COLUMN_CALORIES +
                        ") AS calories, SUM(" +
                        COLUMN_DURATION +
                        ") AS duration " +
                        "FROM " +
                        TABLE_ACTIVITIES +
                        " GROUP BY " +
                        COLUMN_DATE +
                        " ORDER BY " +
                        COLUMN_DATE +
                        " DESC LIMIT 7",
                null
        );
    }

    // ============================================================
    // MONTHLY DATA
    // ============================================================

    public Cursor getCurrentMonth() {

        String month =
                new SimpleDateFormat(
                        "yyyy-MM",
                        Locale.getDefault()
                ).format(new Date());

        return getReadableDatabase().rawQuery(
                "SELECT " +
                        COLUMN_DATE +
                        ", SUM(" +
                        COLUMN_STEPS +
                        "), SUM(" +
                        COLUMN_CALORIES +
                        "), SUM(" +
                        COLUMN_DURATION +
                        ") " +
                        "FROM " +
                        TABLE_ACTIVITIES +
                        " WHERE " +
                        COLUMN_DATE +
                        " LIKE ? " +
                        " GROUP BY " +
                        COLUMN_DATE +
                        " ORDER BY " +
                        COLUMN_DATE,
                new String[]{
                        month + "%"
                }
        );
    }

    // ============================================================
    // SETTINGS
    // ============================================================

    public String getSetting(
            String key,
            String defaultValue) {

        Cursor cursor =
                getReadableDatabase().query(
                        TABLE_SETTINGS,
                        new String[]{
                                COLUMN_SETTING_VALUE
                        },
                        COLUMN_SETTING_KEY + "=?",
                        new String[]{
                                key
                        },
                        null,
                        null,
                        null
                );

        String result = defaultValue;

        if (cursor.moveToFirst()) {
            result = cursor.getString(0);
        }

        cursor.close();

        return result;
    }

    public void setSetting(
            String key,
            String value) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_SETTING_KEY,
                key
        );

        values.put(
                COLUMN_SETTING_VALUE,
                value
        );

        db.insertWithOnConflict(
                TABLE_SETTINGS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public int getIntSetting(
            String key,
            int defaultValue) {

        try {

            return Integer.parseInt(
                    getSetting(
                            key,
                            String.valueOf(defaultValue)
                    )
            );

        } catch (Exception e) {

            return defaultValue;
        }
    }

    // ============================================================
    // GOALS
    // ============================================================

    public int getDailyStepGoal() {

        return getIntSetting(
                KEY_STEP_GOAL,
                DEFAULT_STEP_GOAL
        );
    }

    public int getDailyCalorieGoal() {

        return getIntSetting(
                KEY_CALORIE_GOAL,
                DEFAULT_CALORIE_GOAL
        );
    }

    public int getDailyWorkoutGoal() {

        return getIntSetting(
                KEY_WORKOUT_GOAL,
                DEFAULT_WORKOUT_GOAL
        );
    }

    public void setDailyStepGoal(int value) {

        setSetting(
                KEY_STEP_GOAL,
                String.valueOf(value)
        );
    }

    public void setDailyCalorieGoal(int value) {

        setSetting(
                KEY_CALORIE_GOAL,
                String.valueOf(value)
        );
    }

    public void setDailyWorkoutGoal(int value) {

        setSetting(
                KEY_WORKOUT_GOAL,
                String.valueOf(value)
        );
    }

    // ============================================================
    // ACHIEVEMENTS
    // ============================================================

    public Cursor getAchievements() {

        return getReadableDatabase().query(
                TABLE_ACHIEVEMENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ACHIEVEMENT_ID + " ASC"
        );
    }

    public void unlockAchievement(
            int achievementId) {

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_ACHIEVEMENT_UNLOCKED,
                1
        );

        getWritableDatabase().update(
                TABLE_ACHIEVEMENTS,
                values,
                COLUMN_ACHIEVEMENT_ID + "=?",
                new String[]{
                        String.valueOf(achievementId)
                }
        );
    }

    // ============================================================
    // PERSONAL RECORDS
    // ============================================================

    public void savePersonalRecord(
            String type,
            int value,
            String date) {

        ContentValues values =
                new ContentValues();

        values.put(COLUMN_RECORD_TYPE, type);
        values.put(COLUMN_RECORD_VALUE, value);
        values.put(COLUMN_RECORD_DATE, date);

        getWritableDatabase().insert(
                TABLE_PERSONAL_RECORDS,
                null,
                values
        );
    }

    public Cursor getPersonalRecords() {

        return getReadableDatabase().query(
                TABLE_PERSONAL_RECORDS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_RECORD_VALUE + " DESC"
        );
    }

    // ============================================================
    // BMI DATA
    // ============================================================

    public void saveBMIData(
            double height,
            double weight) {

        setSetting(
                KEY_HEIGHT,
                String.valueOf(height)
        );

        setSetting(
                KEY_WEIGHT,
                String.valueOf(weight)
        );
    }

    public double getHeight() {

        try {

            return Double.parseDouble(
                    getSetting(
                            KEY_HEIGHT,
                            "0"
                    )
            );

        } catch (Exception e) {

            return 0;
        }
    }

    public double getWeight() {

        try {

            return Double.parseDouble(
                    getSetting(
                            KEY_WEIGHT,
                            "0"
                    )
            );

        } catch (Exception e) {

            return 0;
        }
    }
}