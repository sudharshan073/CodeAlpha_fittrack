package com.example.fittrack;

public class ActivityModel {

    private int id;
    private String date;
    private String exercise;
    private int duration;
    private int steps;
    private int calories;

    public ActivityModel(
            int id,
            String date,
            String exercise,
            int duration,
            int steps,
            int calories) {

        this.id = id;
        this.date = date;
        this.exercise = exercise;
        this.duration = duration;
        this.steps = steps;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getExercise() {
        return exercise;
    }

    public int getDuration() {
        return duration;
    }

    public int getSteps() {
        return steps;
    }

    public int getCalories() {
        return calories;
    }
}