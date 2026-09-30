package com.example.fittrack;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private TextView backButton;
    private TextView reportPeriodText;
    private TextView reportStepsText;
    private TextView reportCaloriesText;
    private TextView reportWorkoutText;
    private TextView reportActivityCountText;

    private TextView exportPdfButton;
    private TextView sharePdfButton;
    private TextView exportCsvButton;
    private TextView shareCsvButton;

    private long totalSteps;
    private int totalCalories;
    private int totalWorkout;
    private int activityCount;

    private File lastPdfFile;
    private File lastCsvFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        setupButtons();
        loadReportData();
    }

    // ==================== INITIALIZE VIEWS ====================

    private void initializeViews() {

        backButton = findViewById(R.id.backButton);

        reportPeriodText = findViewById(R.id.reportPeriodText);
        reportStepsText = findViewById(R.id.reportStepsText);
        reportCaloriesText = findViewById(R.id.reportCaloriesText);
        reportWorkoutText = findViewById(R.id.reportWorkoutText);

        reportActivityCountText =
                findViewById(R.id.reportActivityCountText);

        exportPdfButton = findViewById(R.id.exportPdfButton);
        sharePdfButton = findViewById(R.id.sharePdfButton);

        exportCsvButton = findViewById(R.id.exportCsvButton);
        shareCsvButton = findViewById(R.id.shareCsvButton);
    }

    // ==================== BUTTONS ====================

    private void setupButtons() {

        backButton.setOnClickListener(v -> finish());

        exportPdfButton.setOnClickListener(v -> {
            generatePdfReport();
        });

        sharePdfButton.setOnClickListener(v -> {
            sharePdfReport();
        });

        exportCsvButton.setOnClickListener(v -> {
            generateCsvReport();
        });

        shareCsvButton.setOnClickListener(v -> {
            shareCsvReport();
        });
    }

    // ==================== LOAD REPORT DATA ====================

    private void loadReportData() {

        totalSteps = databaseHelper.getTotalSteps();
        totalCalories = databaseHelper.getTotalCalories();
        totalWorkout = databaseHelper.getTotalWorkoutMinutes();
        activityCount = databaseHelper.getActivityCount();

        reportPeriodText.setText(
                "Overall fitness statistics"
        );

        reportStepsText.setText(
                String.format(
                        Locale.getDefault(),
                        "%,d",
                        totalSteps
                )
        );

        reportCaloriesText.setText(
                String.format(
                        Locale.getDefault(),
                        "%,d kcal",
                        totalCalories
                )
        );

        reportWorkoutText.setText(
                String.format(
                        Locale.getDefault(),
                        "%d minutes",
                        totalWorkout
                )
        );

        reportActivityCountText.setText(
                String.valueOf(activityCount)
        );
    }

    // ==================== PDF REPORT ====================

    private void generatePdfReport() {

        PdfDocument pdfDocument = new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(
                        595,
                        842,
                        1
                ).create();

        PdfDocument.Page page =
                pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();

        paint.setTextSize(24);
        paint.setFakeBoldText(true);

        page.getCanvas().drawText(
                "FITTRACK FITNESS REPORT",
                50,
                70,
                paint
        );

        paint.setTextSize(14);
        paint.setFakeBoldText(false);

        String generatedDate = new SimpleDateFormat(
                "dd MMMM yyyy, hh:mm a",
                Locale.getDefault()
        ).format(new Date());

        page.getCanvas().drawText(
                "Generated: " + generatedDate,
                50,
                105,
                paint
        );

        paint.setTextSize(18);
        paint.setFakeBoldText(true);

        page.getCanvas().drawText(
                "Fitness Summary",
                50,
                160,
                paint
        );

        paint.setTextSize(16);
        paint.setFakeBoldText(false);

        page.getCanvas().drawText(
                "Total Steps: " + totalSteps,
                70,
                210,
                paint
        );

        page.getCanvas().drawText(
                "Total Calories: " + totalCalories + " kcal",
                70,
                255,
                paint
        );

        page.getCanvas().drawText(
                "Total Workout: " + totalWorkout + " minutes",
                70,
                300,
                paint
        );

        page.getCanvas().drawText(
                "Total Activities: " + activityCount,
                70,
                345,
                paint
        );

        paint.setTextSize(18);
        paint.setFakeBoldText(true);

        page.getCanvas().drawText(
                "Motivational Message",
                50,
                420,
                paint
        );

        paint.setTextSize(15);
        paint.setFakeBoldText(false);

        page.getCanvas().drawText(
                "Keep moving and stay consistent!",
                70,
                465,
                paint
        );

        page.getCanvas().drawText(
                "Small steps every day lead to big results.",
                70,
                500,
                paint
        );

        paint.setTextSize(12);

        page.getCanvas().drawText(
                "Generated by FitTrack",
                50,
                780,
                paint
        );

        pdfDocument.finishPage(page);

        savePdfFile(pdfDocument);
    }

    private void savePdfFile(PdfDocument pdfDocument) {

        String fileName = "FitTrack_Report_"
                + new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date())
                + ".pdf";

        File reportDirectory = new File(
                getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                ),
                "FitTrack Reports"
        );

        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }

        File pdfFile = new File(
                reportDirectory,
                fileName
        );

        try {

            FileOutputStream outputStream =
                    new FileOutputStream(pdfFile);

            pdfDocument.writeTo(outputStream);

            outputStream.close();
            pdfDocument.close();

            lastPdfFile = pdfFile;

            Toast.makeText(
                    this,
                    "PDF report created successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (IOException e) {

            pdfDocument.close();

            Toast.makeText(
                    this,
                    "Failed to create PDF report",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    // ==================== CSV REPORT ====================

    private void generateCsvReport() {

        String fileName = "FitTrack_Report_"
                + new SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
        ).format(new Date())
                + ".csv";

        File reportDirectory = new File(
                getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                ),
                "FitTrack Reports"
        );

        if (!reportDirectory.exists()) {
            reportDirectory.mkdirs();
        }

        File csvFile = new File(
                reportDirectory,
                fileName
        );

        String generatedDate = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        String csvContent =
                "FitTrack Fitness Report\n"
                        + "Generated Date,"
                        + generatedDate
                        + "\n\n"
                        + "Metric,Value\n"
                        + "Total Steps,"
                        + totalSteps
                        + "\n"
                        + "Total Calories,"
                        + totalCalories
                        + " kcal\n"
                        + "Total Workout,"
                        + totalWorkout
                        + " minutes\n"
                        + "Total Activities,"
                        + activityCount
                        + "\n";

        try {

            FileOutputStream outputStream =
                    new FileOutputStream(csvFile);

            outputStream.write(
                    csvContent.getBytes()
            );

            outputStream.close();

            lastCsvFile = csvFile;

            Toast.makeText(
                    this,
                    "CSV report created successfully",
                    Toast.LENGTH_LONG
            ).show();

        } catch (IOException e) {

            Toast.makeText(
                    this,
                    "Failed to create CSV report",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    // ==================== SHARE PDF ====================

    private void sharePdfReport() {

        if (lastPdfFile == null || !lastPdfFile.exists()) {

            Toast.makeText(
                    this,
                    "Please export the PDF report first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        shareFile(
                lastPdfFile,
                "application/pdf",
                "Share FitTrack PDF Report"
        );
    }

    // ==================== SHARE CSV ====================

    private void shareCsvReport() {

        if (lastCsvFile == null || !lastCsvFile.exists()) {

            Toast.makeText(
                    this,
                    "Please export the CSV report first",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        shareFile(
                lastCsvFile,
                "text/csv",
                "Share FitTrack CSV Report"
        );
    }

    // ==================== SHARE FILE ====================

    private void shareFile(
            File file,
            String mimeType,
            String chooserTitle
    ) {

        try {

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent shareIntent = new Intent(
                    Intent.ACTION_SEND
            );

            shareIntent.setType(mimeType);

            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    fileUri
            );

            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            chooserTitle
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to share report",
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();
        }
    }

    // ==================== LIFECYCLE ====================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadReportData();
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