package com.example.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psp.LearningEffort;
import com.example.psp.LearningUnit;
import com.example.psp.NewEditLearningEffort;
import com.example.psp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class LearningEffortAdapter extends RecyclerView.Adapter<LearningEffortAdapter.LearningEffortViewHolder> {
    private final ArrayList<LearningEffort> learningEfforts;
    private final LearningUnit learningUnit;

    public LearningEffortAdapter(ArrayList<LearningEffort> learningEfforts, LearningUnit learningUnit) {
        this.learningEfforts = learningEfforts;
        this.learningUnit = learningUnit;
    }

    @NonNull
    @Override
    public LearningEffortViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_learning_effort, parent, false);
        return new LearningEffortViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LearningEffortViewHolder holder, int position) {
        LearningEffort learningEffort = learningEfforts.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat(holder.itemView.getContext().getString(R.string.date_time_dp));
        String learningEffortDate = sdf.format(learningEffort.getLearningEffortDate());
        holder.learningEffortDateTextView.setText(learningEffortDate);

        String actualLearningEffort = String.format(holder.itemView.getContext().getString(R.string.learning_effort_time_dp), learningEffort.getActualLearningEffortHours(), learningEffort.getActualLearningEffortMinutes());
        holder.learningEffortActualTextView.setText(actualLearningEffort);

        // Der OnClickListener funktioniert so wie das onClick-Attribut bei den Buttons.
        // Hierbei beziehe ich mich jedoch auf die ImageView innerhalb des Items.
        holder.learningEffortCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), NewEditLearningEffort.class);
                intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
                intent.putExtra("LEARNING_EFFORT_OBJECT", learningEffort);
                intent.putExtra("MODE", "EDIT");
                view.getContext().startActivity(intent);

                ((Activity) view.getContext()).overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return learningEfforts.size();
    }
    public static class LearningEffortViewHolder extends RecyclerView.ViewHolder {
        private final TextView learningEffortDateTextView;
        private final TextView learningEffortActualTextView;
        private final ImageView learningEffortEditImageView;
        private final CardView learningEffortCardView;

        public LearningEffortViewHolder(View view) {
            super(view);

            learningEffortDateTextView =  view.findViewById(R.id.itemLearningEffortTimestampTextView);
            learningEffortActualTextView = view.findViewById(R.id.itemActualLearningEffortTextView);
            learningEffortEditImageView = view.findViewById(R.id.itemLearningEffortDetailsImageView);
            learningEffortCardView = view.findViewById(R.id.itemLearningEffortCardView);
        }
    }
}
