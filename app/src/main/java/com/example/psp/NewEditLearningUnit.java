package com.example.psp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.LearningUnitDataSource;

public class NewEditLearningUnit extends AppCompatActivity {

    private LearningUnitDataSource learningUnitDataSource;
    private String mode;
    private com.example.psp.LearningUnit learningUnit;
    private com.example.psp.Course course;

    private EditText learningUnitTitleEditText;
    private NumberPicker planedLearningEffortHoursNumberPicker;
    private NumberPicker planedLearningEffortMinutesNumberPicker;
    private String learningUnitTitle;
    private int learningEffortPlannedHours;
    private int learningEffortPlannedMinutes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_learning_unit);

        learningUnitDataSource = new LearningUnitDataSource(this);

        mode = getIntent().getStringExtra("MODE");
        course = (com.example.psp.Course) getIntent().getSerializableExtra("COURSE_OBJECT");

        if (savedInstanceState != null) {
            course = (com.example.psp.Course) savedInstanceState.getSerializable("COURSE_OBJECT");
            learningUnit = (com.example.psp.LearningUnit) savedInstanceState.getSerializable("LEARNING_UNIT_OBJECT");
        }

        learningUnitTitleEditText = findViewById(R.id.learningUnitTitleEditText);
        planedLearningEffortHoursNumberPicker = findViewById(R.id.learningEffortPlannedHoursNumberPicker);
        planedLearningEffortMinutesNumberPicker = findViewById(R.id.learningEffortPlannedMinutesNumberPicker);

        initializeNumberPickerValues();

        findViewById(R.id.newEditLearningUnitConstraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return true;
            }
        });

        if(mode.equals("EDIT")) {
            Button newEditLearningUnitButton = findViewById(R.id.newEditLearningUnitButton);
            newEditLearningUnitButton.setText(R.string.save_learning_unit);

            if (learningUnit == null) {
                learningUnit = (com.example.psp.LearningUnit) getIntent().getSerializableExtra("LEARNING_UNIT_OBJECT");
            }

            int plannedLearningEffortHours = learningUnit.getPlannedLearningEffortHours();
            int plannedLearningEffortMinutes = learningUnit.getPlannedLearningEffortMinutes();
            planedLearningEffortHoursNumberPicker.setValue(plannedLearningEffortHours);
            planedLearningEffortMinutesNumberPicker.setValue(plannedLearningEffortMinutes);

            learningUnitTitleEditText.setText(learningUnit.getLearningUnitTitle());
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("COURSE_OBJECT", course);
        outState.putSerializable("LEARNING_UNIT_OBJECT", learningUnit);
    }


    private void initializeNumberPickerValues() {
        String[] valuesHours = new String[100];
        String[] valuesMinutes = new String[60];

        planedLearningEffortHoursNumberPicker.setMinValue(0);
        planedLearningEffortHoursNumberPicker.setMaxValue(99);

        planedLearningEffortMinutesNumberPicker.setMinValue(0);
        planedLearningEffortMinutesNumberPicker.setMaxValue(59);

        for(int i = 0; i < 100; i++) {
            valuesHours[i] = String.format("%02d", i);
        }
        planedLearningEffortHoursNumberPicker.setDisplayedValues(valuesHours);

        for(int j = 0; j < 60; j++) {
            valuesMinutes[j] = String.format("%02d", j);
        }
        planedLearningEffortMinutesNumberPicker.setDisplayedValues(valuesMinutes);
    }

    public void saveLearningUnitButtonClicked(View view) {

        if(areFieldsEmpty()) {
            String message = getResources().getString(R.string.toast_error_edittexts_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            learningUnitTitle = learningUnitTitleEditText.getText().toString().trim();

            learningEffortPlannedHours =  planedLearningEffortHoursNumberPicker.getValue();
            learningEffortPlannedMinutes = planedLearningEffortMinutesNumberPicker.getValue();

            if(mode.equals("NEW")) {
                newSaveLearningUnit();
            } else if(mode.equals("EDIT")) {
                editSaveLearningUnit();
            }
        }
    }

    private void newSaveLearningUnit() {
        learningUnitDataSource.addLearningUnit(learningUnitTitle, learningEffortPlannedHours, learningEffortPlannedMinutes, course.getCourseId());
        String message = getResources().getString(R.string.toast_new_learning_unit);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CourseDetails.class);
        intent.putExtra("COURSE_OBJECT", course);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    private void editSaveLearningUnit() {
        learningUnit.setLearningUnitTitle(learningUnitTitle);
        learningUnit.setPlannedLearningEffort(learningEffortPlannedHours, learningEffortPlannedMinutes);

        learningUnitDataSource.updateLearningUnit(learningUnit);
        String message = getResources().getString(R.string.toast_edit_learning_unit);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LearningUnitDetails.class);
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }


    private boolean areFieldsEmpty() {
        return TextUtils.isEmpty(learningUnitTitleEditText.getText().toString().trim())
                || (planedLearningEffortHoursNumberPicker.getValue() == 0) && (planedLearningEffortMinutesNumberPicker.getValue() == 0);
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }
    @Override
    protected void onDestroy() {
        learningUnitDataSource.close();
        super.onDestroy();
    }

    // Methode zum Verbergen der Tastatur
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}