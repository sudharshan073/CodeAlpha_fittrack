package com.example.fittrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<ActivityModel> activityList;
    private final OnActivityActionListener listener;

    public interface OnActivityActionListener {
        void onEdit(ActivityModel activity);
        void onDelete(ActivityModel activity);
    }

    public HistoryAdapter(
            List<ActivityModel> activityList,
            OnActivityActionListener listener) {

        this.activityList = activityList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_activity,
                        parent,
                        false
                );

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoryViewHolder holder,
            int position) {

        if (position < 0 || position >= activityList.size()) {
            return;
        }

        ActivityModel activity = activityList.get(position);

        holder.exerciseText.setText(
                activity.getExercise()
        );

        holder.dateText.setText(
                activity.getDate()
        );

        holder.durationText.setText(
                formatNumber(activity.getDuration()) + " min"
        );

        holder.stepsText.setText(
                formatNumber(activity.getSteps()) + " steps"
        );

        holder.caloriesText.setText(
                formatNumber(activity.getCalories()) + " kcal"
        );

        holder.editButton.setOnClickListener(v -> {

            if (listener != null) {
                listener.onEdit(activity);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {

            if (listener != null) {
                listener.onDelete(activity);
            }
        });
    }

    private String formatNumber(int number) {

        return NumberFormat.getIntegerInstance(
                Locale.getDefault()
        ).format(number);
    }

    @Override
    public int getItemCount() {

        if (activityList == null) {
            return 0;
        }

        return activityList.size();
    }

    static class HistoryViewHolder
            extends RecyclerView.ViewHolder {

        TextView dateText;
        TextView exerciseText;
        TextView durationText;
        TextView stepsText;
        TextView caloriesText;
        TextView editButton;
        TextView deleteButton;

        public HistoryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            dateText = itemView.findViewById(
                    R.id.dateText
            );

            exerciseText = itemView.findViewById(
                    R.id.exerciseText
            );

            durationText = itemView.findViewById(
                    R.id.durationText
            );

            stepsText = itemView.findViewById(
                    R.id.stepsText
            );

            caloriesText = itemView.findViewById(
                    R.id.caloriesText
            );

            editButton = itemView.findViewById(
                    R.id.editButton
            );

            deleteButton = itemView.findViewById(
                    R.id.deleteButton
            );
        }
    }
}