package com.example.fittrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityAdapter
        extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final List<ActivityModel> activityList;

    private final OnActivityActionListener listener;


    // =====================================================
    // ACTION LISTENER
    // =====================================================

    public interface OnActivityActionListener {

        void onEdit(ActivityModel activity);

        void onDelete(ActivityModel activity);
    }


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public ActivityAdapter(
            List<ActivityModel> activityList,
            OnActivityActionListener listener) {

        this.activityList = activityList;

        this.listener = listener;
    }


    // =====================================================
    // CREATE VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_activity,
                        parent,
                        false
                );

        return new ActivityViewHolder(view);
    }


    // =====================================================
    // BIND DATA
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ActivityViewHolder holder,
            int position) {

        ActivityModel activity =
                activityList.get(position);


        // ==============================
        // DATE
        // ==============================

        holder.dateText.setText(
                activity.getDate()
        );


        // ==============================
        // EXERCISE
        // ==============================

        holder.exerciseText.setText(
                activity.getExercise()
        );


        // ==============================
        // DURATION
        // ==============================

        holder.durationText.setText(
                activity.getDuration()
                        + " min"
        );


        // ==============================
        // STEPS
        // ==============================

        holder.stepsText.setText(
                activity.getSteps()
                        + " steps"
        );


        // ==============================
        // CALORIES
        // ==============================

        holder.caloriesText.setText(
                activity.getCalories()
                        + " kcal"
        );


        // ==============================
        // EDIT BUTTON
        // ==============================

        holder.editButton.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onEdit(
                                activity
                        );
                    }
                }
        );


        // ==============================
        // DELETE BUTTON
        // ==============================

        holder.deleteButton.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onDelete(
                                activity
                        );
                    }
                }
        );
    }


    // =====================================================
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        if (activityList == null) {

            return 0;
        }

        return activityList.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ActivityViewHolder
            extends RecyclerView.ViewHolder {

        TextView dateText;

        TextView exerciseText;

        TextView durationText;

        TextView stepsText;

        TextView caloriesText;

        TextView editButton;

        TextView deleteButton;


        // =================================================
        // CONSTRUCTOR
        // =================================================

        public ActivityViewHolder(
                @NonNull View itemView) {

            super(itemView);


            // ==============================
            // DATE
            // ==============================

            dateText =
                    itemView.findViewById(
                            R.id.dateText
                    );


            // ==============================
            // EXERCISE
            // ==============================

            exerciseText =
                    itemView.findViewById(
                            R.id.exerciseText
                    );


            // ==============================
            // DURATION
            // ==============================

            durationText =
                    itemView.findViewById(
                            R.id.durationText
                    );


            // ==============================
            // STEPS
            // ==============================

            stepsText =
                    itemView.findViewById(
                            R.id.stepsText
                    );


            // ==============================
            // CALORIES
            // ==============================

            caloriesText =
                    itemView.findViewById(
                            R.id.caloriesText
                    );


            // ==============================
            // EDIT
            // ==============================

            editButton =
                    itemView.findViewById(
                            R.id.editButton
                    );


            // ==============================
            // DELETE
            // ==============================

            deleteButton =
                    itemView.findViewById(
                            R.id.deleteButton
                    );
        }
    }
}