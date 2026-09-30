package com.example.fittrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity
        implements HistoryAdapter.OnActivityActionListener {

    private EditText searchEditText;
    private EditText filterDateEditText;

    private TextView clearFilterButton;
    private TextView activityCountText;
    private TextView emptyText;
    private TextView backButton;

    private RecyclerView historyRecyclerView;

    private DatabaseHelper databaseHelper;

    private HistoryAdapter adapter;

    private final List<ActivityModel> allActivities =
            new ArrayList<>();

    private final List<ActivityModel> filteredActivities =
            new ArrayList<>();

    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupRecyclerView();
        setupSearch();
        setupDateFilter();
        setupButtons();

        loadActivities();
    }

    private void initializeViews() {

        searchEditText =
                findViewById(R.id.searchEditText);

        filterDateEditText =
                findViewById(R.id.filterDateEditText);

        clearFilterButton =
                findViewById(R.id.clearFilterButton);

        activityCountText =
                findViewById(R.id.activityCountText);

        emptyText =
                findViewById(R.id.emptyText);

        backButton =
                findViewById(R.id.backButton);

        historyRecyclerView =
                findViewById(R.id.historyRecyclerView);
    }

    private void setupRecyclerView() {

        adapter =
                new HistoryAdapter(
                        filteredActivities,
                        this
                );

        historyRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        historyRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {

        searchEditText.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        applyFilters();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void setupDateFilter() {

        filterDateEditText.setOnClickListener(v -> {

            Calendar calendar =
                    Calendar.getInstance();

            if (!selectedDate.isEmpty()) {

                try {

                    DateTimeHelper.parseDate(
                            selectedDate
                    );

                } catch (Exception ignored) {
                }
            }

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            HistoryActivity.this,
                            (view, year, month, dayOfMonth) -> {

                                selectedDate =
                                        String.format(
                                                Locale.getDefault(),
                                                "%04d-%02d-%02d",
                                                year,
                                                month + 1,
                                                dayOfMonth
                                        );

                                filterDateEditText.setText(
                                        selectedDate
                                );

                                applyFilters();
                            },
                            calendar.get(
                                    Calendar.YEAR
                            ),
                            calendar.get(
                                    Calendar.MONTH
                            ),
                            calendar.get(
                                    Calendar.DAY_OF_MONTH
                            )
                    );

            dialog.show();
        });
    }

    private void setupButtons() {

        backButton.setOnClickListener(v ->
                finish()
        );

        clearFilterButton.setOnClickListener(v -> {

            searchEditText.setText("");
            filterDateEditText.setText("");
            selectedDate = "";

            applyFilters();
        });
    }

    private void loadActivities() {

        allActivities.clear();

        Cursor cursor =
                databaseHelper.getAllActivities();

        if (cursor != null) {

            try {

                while (cursor.moveToNext()) {

                    int id =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_ID
                                    )
                            );

                    String date =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_DATE
                                    )
                            );

                    String exercise =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_EXERCISE
                                    )
                            );

                    int duration =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COLUMN_DURATION
                                    )
                            );

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

                    ActivityModel activity =
                            new ActivityModel(
                                    id,
                                    date,
                                    exercise,
                                    duration,
                                    steps,
                                    calories
                            );

                    allActivities.add(activity);
                }

            } finally {

                cursor.close();
            }
        }

        applyFilters();
    }

    private void applyFilters() {

        String searchText =
                searchEditText.getText()
                        .toString()
                        .trim()
                        .toLowerCase(Locale.getDefault());

        filteredActivities.clear();

        for (ActivityModel activity : allActivities) {

            boolean matchesSearch = true;
            boolean matchesDate = true;

            if (!searchText.isEmpty()) {

                String exercise =
                        activity.getExercise()
                                .toLowerCase(
                                        Locale.getDefault()
                                );

                matchesSearch =
                        exercise.contains(searchText);
            }

            if (!selectedDate.isEmpty()) {

                matchesDate =
                        selectedDate.equals(
                                activity.getDate()
                        );
            }

            if (matchesSearch && matchesDate) {

                filteredActivities.add(activity);
            }
        }

        adapter.notifyDataSetChanged();

        updateEmptyState();
    }

    private void updateEmptyState() {

        int count =
                filteredActivities.size();

        if (count == 0) {

            historyRecyclerView.setVisibility(
                    View.GONE
            );

            emptyText.setVisibility(
                    View.VISIBLE
            );

            if (allActivities.isEmpty()) {

                emptyText.setText(
                        "No activities recorded yet"
                );

            } else {

                emptyText.setText(
                        "No activities match your search"
                );
            }

        } else {

            historyRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyText.setVisibility(
                    View.GONE
            );
        }

        String label =
                count == 1
                        ? "1 activity"
                        : count + " activities";

        activityCountText.setText(label);
    }

    @Override
    public void onEdit(ActivityModel activity) {

        Intent intent =
                new Intent(
                        HistoryActivity.this,
                        AddActivity.class
                );

        intent.putExtra(
                "activity_id",
                activity.getId()
        );

        startActivity(intent);
    }

    @Override
    public void onDelete(ActivityModel activity) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Activity")
                .setMessage(
                        "Are you sure you want to delete this activity?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            deleteActivity(
                                    activity
                            );
                        }
                )
                .show();
    }

    private void deleteActivity(
            ActivityModel activity) {

        int result =
                databaseHelper.deleteActivity(
                        activity.getId()
                );

        if (result > 0) {

            Toast.makeText(
                    this,
                    "Activity deleted",
                    Toast.LENGTH_SHORT
            ).show();

            loadActivities();

        } else {

            Toast.makeText(
                    this,
                    "Unable to delete activity",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadActivities();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    /*
     * Small internal helper used only for safely
     * handling the selected date.
     */
    private static class DateTimeHelper {

        static void parseDate(String date)
                throws Exception {

            new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
            ).parse(date);
        }
    }
}