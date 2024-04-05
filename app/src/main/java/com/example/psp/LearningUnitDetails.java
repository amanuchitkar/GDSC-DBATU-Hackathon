package com.example.psp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.psp.R;
import com.example.adapter.LearningEffortAdapter;
import com.example.db.LearningEffortDataSource;
import com.example.db.LearningUnitDataSource;
import com.example.psp.LearningEffort;
import com.example.psp.LearningUnit;

import java.util.ArrayList;

public class LearningUnitDetails extends AppCompatActivity {

    private LearningEffortDataSource learningEffortDataSource;

    LearningEffortAdapter learningEffortAdapter;

    LearningUnit learningUnit;

    private ArrayList<LearningEffort> learningEfforts;

    private int sumHours;
    private int sumMinutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_unit_details);

        learningEffortDataSource = new LearningEffortDataSource(this);

        if (savedInstanceState != null) {
            learningUnit = (LearningUnit) savedInstanceState.getSerializable("LEARNING_UNIT_OBJECT");

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("LEARNING_UNIT_OBJECT", learningUnit);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (learningUnit == null) {
            learningUnit = (LearningUnit) getIntent().getSerializableExtra("LEARNING_UNIT_OBJECT");
        }

        learningEfforts = learningEffortDataSource.getLearningEffortsForLearningUnit(learningUnit.getLearningUnitId());

        setLearningUnitTextViews();
        setLearningEffortCurrentTextView();
        initializeRecyclerView();

    }

    private void setLearningUnitTextViews() {

        TextView learningUnitTitleTextView = findViewById(R.id.learningUnitTitleTextView);
        TextView plannedLearningEffortTextView = findViewById(R.id.learningEffortPlannedTextView);

        learningUnitTitleTextView.setText(learningUnit.getLearningUnitTitle());
        plannedLearningEffortTextView.setText(this.getString(R.string.planned_dp, learningUnit.getPlannedLearningEffortHours(), learningUnit.getPlannedLearningEffortMinutes()));
    }

    public void changeLearningUnitButtonClicked(View view) {
        Intent intent = new Intent(this, NewEditLearningUnit.class);
        intent.putExtra("MODE", "EDIT");
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    private void deleteLearningUnit () {
        LearningUnitDataSource learningUnitDataSource = new LearningUnitDataSource(this);
        learningUnitDataSource.removeLearningUnit(learningUnit);

        String message = getResources().getString(R.string.toast_learning_unit_deleted);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        learningUnitDataSource.close();
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    public void addLearningEffortButtonClicked(View view) {
        Intent intent = new Intent(this, NewEditLearningEffort.class);
        intent.putExtra("MODE", "NEW");
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.learningUnitDetailsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        learningEffortAdapter = new LearningEffortAdapter(learningEfforts, learningUnit);
        recyclerView.setAdapter(learningEffortAdapter);
    }

    private void setLearningEffortCurrentTextView() {
        long actualLearningEffortsSum = 0;

        for (LearningEffort learningEffort : learningEfforts) {
            actualLearningEffortsSum += learningEffort.getActualLearningEffort();
        }
        sumHours =  LearningUnit.calculateLearningEffortHours(actualLearningEffortsSum);
        sumMinutes = LearningUnit.calculateLearningEffortMinutes(actualLearningEffortsSum);

        TextView currentLearningEffortTextView = findViewById(R.id.learningEffortCurrentTextView);
        currentLearningEffortTextView.setText(this.getString(R.string.current_dp, sumHours, sumMinutes));
    }

    public void newLearningEffortStopwatchButtonClicked(View view) {
        Intent intent = new Intent(this, NewLearningEffortStopwatch.class);
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        intent.putExtra("LEARNING_EFFORT_SUM_HOURS", sumHours);
        intent.putExtra("LEARNING_EFFORT_SUM_MINUTES", sumMinutes);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
    }

    @Override
    protected void onDestroy() {

        learningEffortDataSource.close();
        super.onDestroy();
    }

    public void deleteLearningUnitButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_deletion_title);
        builder.setMessage(String.format(getString(R.string.alert_dialog_deletion_question_learning_unit), learningUnit.getLearningUnitTitle()));

        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteLearningUnit();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}