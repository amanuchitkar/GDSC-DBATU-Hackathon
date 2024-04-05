package com.example.psp;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.LearningEffortDataSource;

import java.util.Date;

public class NewLearningEffortStopwatch extends AppCompatActivity {

    private LearningEffortDataSource learningEffortDataSource;

    private com.example.psp.LearningUnit learningUnit;
    private int sumHours;
    private int sumMinutes;

    private Chronometer stopwatchChronometer;
    Button startButton;
    Button stopButton;
    private long timeAtStop;
    private boolean isStopwatchRunning;
    private boolean resetState;

    int stopwatchTimeHours;
    int stopwatchTimeMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_learning_effort_stopwatch);

        learningEffortDataSource = new LearningEffortDataSource(this);

        if (savedInstanceState != null) {
            learningUnit = (com.example.psp.LearningUnit) savedInstanceState.getSerializable("LEARNING_UNIT_OBJECT");
            sumHours = savedInstanceState.getInt("LEARNING_EFFORT_SUM_HOURS", 0);
            sumMinutes = savedInstanceState.getInt("LEARNING_EFFORT_SUM_MINUTES", 0);
        }

        stopwatchChronometer = findViewById(R.id.learningEffortStopwatchChronometer);
        startButton = findViewById(R.id.startStopwatchButton);
        stopButton = findViewById(R.id.stopStopwatchButton);

        resetState = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (learningUnit == null) {
            learningUnit = (com.example.psp.LearningUnit) getIntent().getSerializableExtra("LEARNING_UNIT_OBJECT");
        }

        if (sumHours == 0 && sumMinutes == 0) {
            sumHours = getIntent().getIntExtra("LEARNING_EFFORT_SUM_HOURS", 0);
            sumMinutes = getIntent().getIntExtra("LEARNING_EFFORT_SUM_MINUTES", 0);
        }

        setLearningUnitTextViews();
        setLearningEffortCurrentTextView();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("LEARNING_UNIT_OBJECT", learningUnit);
        outState.putInt("LEARNING_EFFORT_SUM_HOURS", sumHours);
        outState.putInt("LEARNING_EFFORT_SUM_MINUTES", sumMinutes);
    }

    private void setLearningUnitTextViews() {

        TextView learningUnitTitleTextView = findViewById(R.id.learningUnitTitleTextView);
        TextView plannedLearningEffortTextView = findViewById(R.id.learningEffortPlannedTextView);

        learningUnitTitleTextView.setText(learningUnit.getLearningUnitTitle());
        plannedLearningEffortTextView.setText(this.getString(R.string.planned_dp, learningUnit.getPlannedLearningEffortHours(), learningUnit.getPlannedLearningEffortMinutes()));
    }

    private void setLearningEffortCurrentTextView() {

        TextView currentLearningEffortTextView = findViewById(R.id.learningEffortCurrentTextView);
        currentLearningEffortTextView.setText(this.getString(R.string.current_dp, sumHours, sumMinutes));
    }

    public void startStopwatchButtonClicked(View view) {
        if (!isStopwatchRunning) {

            stopwatchChronometer.setBase(SystemClock.elapsedRealtime() - timeAtStop);

            stopwatchChronometer.start();
            isStopwatchRunning = true;

            resetState = false;
            updateStopwatchButtonTexts();

            String message = getResources().getString(R.string.toast_stopwatch_started);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void stopStopwatchButtonClicked(View view) {
        if (isStopwatchRunning) {

            stopwatchChronometer.stop();
            isStopwatchRunning = false;

            timeAtStop = SystemClock.elapsedRealtime() - stopwatchChronometer.getBase();

            updateStopwatchButtonTexts();
        } else {

            timeAtStop = 0;
            stopwatchChronometer.setBase(SystemClock.elapsedRealtime());

            resetState = true;

            updateStopwatchButtonTexts();
        }
    }

    private void updateStopwatchButtonTexts() {
        if (!isStopwatchRunning) {
            startButton.setText(R.string.continue_timer);
            stopButton.setText(R.string.reset_timer);
        }
        if (isStopwatchRunning || resetState) {
            startButton.setText(R.string.start_timer);
            stopButton.setText(R.string.stop_timer);
        }
    }

    private void getStopwatchTimeValues() {
        String stopwatchTime = stopwatchChronometer.getText().toString();
        String[] stopwatchTimeArray = stopwatchTime.split(":");

        // Es gibt zwei Formate, mm:ss und hh:mm:ss, falls bereits eine Stunde verstrichen ist. Das muss geprüft werden.
        if (stopwatchTimeArray.length == 2) {
            // Format mm:ss
            stopwatchTimeHours = 0;
            stopwatchTimeMinutes = Integer.parseInt(stopwatchTimeArray[0]);

            int seconds = Integer.parseInt(stopwatchTimeArray[1]);
            if (seconds >= 30 || stopwatchTimeMinutes == 0) {
                stopwatchTimeMinutes += 1;
            }
        } else if (stopwatchTimeArray.length == 3) {
            // Format hh:mm:ss
            stopwatchTimeHours = Integer.parseInt(stopwatchTimeArray[0]);
            stopwatchTimeMinutes = Integer.parseInt(stopwatchTimeArray[1]);
            int seconds = Integer.parseInt(stopwatchTimeArray[2]);
            if (seconds >= 30) {
                stopwatchTimeMinutes += 1;
            }
        }
    }

    public void saveLearningEffortButtonClicked(View view) {

        if(isStopwatchRunning) {
            String message = getResources().getString(R.string.toast_error_stopwatch_running);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else if (resetState) {
            String message = getResources().getString(R.string.toast_error_stopwatch_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {

            getStopwatchTimeValues();

            learningEffortDataSource.addLearningEffort(new Date(), stopwatchTimeHours, stopwatchTimeMinutes, learningUnit.getLearningUnitId());
            String message = getResources().getString(R.string.toast_new_learning_effort);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LearningUnitDetails.class);
            intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
        }
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    @Override
    protected void onDestroy() {
        learningEffortDataSource.close();
        super.onDestroy();
    }
}