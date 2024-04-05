package com.example.psp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.LearningEffortDataSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewEditLearningEffort extends AppCompatActivity {

    private LearningEffortDataSource learningEffortDataSource;
    private String mode;
    private com.example.psp.LearningEffort learningEffort;
    private com.example.psp.LearningUnit learningUnit;

    private TextView selectedDateTextView;
    private TextView selectedTimeTextView;
    private NumberPicker actualLearningEffortHoursNumberPicker;
    private NumberPicker actualLearningEffortMinutesNumberPicker;

    private Date learningEffortDate;
    private int learningEffortActualHours;
    private int learningEffortActualMinutes;
    private String tempDateString;
    private String tempTimeString;

    private SimpleDateFormat sdfDate;
    private SimpleDateFormat sdfTime;
    private SimpleDateFormat sdfDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_learning_effort);

        learningEffortDataSource = new LearningEffortDataSource(this);

        mode = getIntent().getStringExtra("MODE");
        learningUnit = (com.example.psp.LearningUnit) getIntent().getSerializableExtra("LEARNING_UNIT_OBJECT");

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        actualLearningEffortHoursNumberPicker = findViewById(R.id.learningEffortActualHoursNumberPicker);
        actualLearningEffortMinutesNumberPicker = findViewById(R.id.learningEffortActualMinutesNumberPicker);

        initializeNumberPickerValues();
        initializeChangeDateButton();
        initializeChangeTimeButton();

        sdfDate = new SimpleDateFormat(getString(R.string.sdf_date_format));
        sdfTime = new SimpleDateFormat(getString(R.string.sdf_time_format));
        sdfDateTime = new SimpleDateFormat(getString(R.string.sdf_standard_format_date));

        if (savedInstanceState != null) {
            learningUnit = (com.example.psp.LearningUnit) savedInstanceState.getSerializable("LEARNING_UNIT_OBJECT");
            learningEffort = (com.example.psp.LearningEffort) savedInstanceState.getSerializable("LEARNING_EFFORT_OBJECT");
        }

        findViewById(R.id.newEditLearningEffortConstraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return true;
            }
        });

        if(mode.equals("NEW")) {
            ImageButton learningEffortDeleteButton = findViewById(R.id.deleteLearningEffortButton);
            learningEffortDeleteButton.setVisibility(View.GONE); //Damit der DeleteButton fehlt.

            learningEffortDate = new Date();
        }

        if(mode.equals("EDIT")) {
            Button newEditLearningUnitButton = findViewById(R.id.newEditLearningEffortButton);
            newEditLearningUnitButton.setText(R.string.save_learning_effort);

            if (learningEffort == null) {
                learningEffort = (com.example.psp.LearningEffort) getIntent().getSerializableExtra("LEARNING_EFFORT_OBJECT");
            }

            int actualLearningEffortHours = learningEffort.getActualLearningEffortHours();
            int actualLearningEffortMinutes = learningEffort.getActualLearningEffortMinutes();
            actualLearningEffortHoursNumberPicker.setValue(actualLearningEffortHours);
            actualLearningEffortMinutesNumberPicker.setValue(actualLearningEffortMinutes);

            learningEffortDate = learningEffort.getLearningEffortDate();
        }

        initializeTempDateTimeStrings();
        updateTimestampStrings();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("LEARNING_UNIT_OBJECT", learningUnit);
        outState.putSerializable("LEARNING_EFFORT_OBJECT", learningUnit);
    }

    private void updateTimestampStrings() {
        String timestampDateString = String.format(getString(R.string.date_dp),sdfDate.format(learningEffortDate));
        String timestampTimeString = String.format(getString(R.string.time_dp),sdfTime.format(learningEffortDate));
        selectedDateTextView.setText(timestampDateString);
        selectedTimeTextView.setText(timestampTimeString);
    }

    private void initializeTempDateTimeStrings() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(learningEffortDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        tempDateString = year + "-" + (month + 1) + "-" + day;
        tempTimeString = hour + ":" + minute;
    }

    private void initializeChangeDateButton() {
        Button selectDateButton = findViewById(R.id.changeDateButton);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEditLearningEffort.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {

                        tempDateString = (selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay);
                        updateLearningEffortDate();
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void initializeChangeTimeButton() {
        Button selectTimeButton = findViewById(R.id.changeTimeButton);
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Damit dynamisch ermittelt werden kann, ob die Zeitauswahl für den Benutzer im 12- oder 24-Stunden-Modus erfolgt.
                // Da aktuell nur deutsch als alternative Sprache neben englisch angeboten wird, passt das. Ist jedoch vermutlich sauberer lösbar.
                boolean is24HourFormat;
                is24HourFormat = Locale.getDefault().getLanguage().equals("de");
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewEditLearningEffort.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        tempTimeString = (selectedHour + ":" + selectedMinute);
                        updateLearningEffortDate();
                    }
                }, hour, minute, is24HourFormat);
                timePickerDialog.show();
            }
        });
    }

    private void updateLearningEffortDate() {
        String tempDateTimeString = tempDateString + " " + tempTimeString + ":00";

        try {
            learningEffortDate = sdfDateTime.parse(tempDateTimeString);
            updateTimestampStrings();

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }


    private void initializeNumberPickerValues() {
        String[] valuesHours = new String[100];
        String[] valuesMinutes = new String[60];

        actualLearningEffortHoursNumberPicker.setMinValue(0);
        actualLearningEffortHoursNumberPicker.setMaxValue(99);

        actualLearningEffortMinutesNumberPicker.setMinValue(0);
        actualLearningEffortMinutesNumberPicker.setMaxValue(59);

        for(int i = 0; i < 100; i++) {
            valuesHours[i] = String.format("%02d", i);
        }
        actualLearningEffortHoursNumberPicker.setDisplayedValues(valuesHours);

        for(int j = 0; j < 60; j++) {
            valuesMinutes[j] = String.format("%02d", j);
        }
        actualLearningEffortMinutesNumberPicker.setDisplayedValues(valuesMinutes);
    }

    public void saveLearningEffortButtonClicked(View view) {

        if(areFieldsEmpty()) {
            String message = getResources().getString(R.string.toast_error_edittexts_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {

            learningEffortActualHours =  actualLearningEffortHoursNumberPicker.getValue();
            learningEffortActualMinutes = actualLearningEffortMinutesNumberPicker.getValue();

            if(mode.equals("NEW")) {
                newSaveLearningEffort();
            } else if(mode.equals("EDIT")) {
                editSaveLearningEffort();
            }
        }
    }

    private void newSaveLearningEffort() {
        learningEffortDataSource.addLearningEffort(learningEffortDate, learningEffortActualHours, learningEffortActualMinutes, learningUnit.getLearningUnitId());
        String message = getResources().getString(R.string.toast_new_learning_effort);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LearningUnitDetails.class);
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    private void editSaveLearningEffort() {
        learningEffort.setActualLearningEffort(learningEffortActualHours, learningEffortActualMinutes);
        learningEffort.setLearningEffortDate(learningEffortDate);

        learningEffortDataSource.updateLearningEffort(learningEffort);
        String message = getResources().getString(R.string.toast_edit_learning_effort);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LearningUnitDetails.class);
        intent.putExtra("LEARNING_UNIT_OBJECT", learningUnit);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }


    private boolean areFieldsEmpty() {
        return selectedDateTextView.getText().toString().equals(getString(R.string.string_placeholder))
                || selectedTimeTextView.getText().toString().equals(getString(R.string.string_placeholder))
                || (actualLearningEffortHoursNumberPicker.getValue() == 0) && (actualLearningEffortMinutesNumberPicker.getValue() == 0);
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    private void deleteLearningEffort(){
        learningEffortDataSource.removeLearningEffort(learningEffort);

        String message = getResources().getString(R.string.toast_learning_effort_deleted);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }
    @Override
    protected void onDestroy() {
        learningEffortDataSource.close();
        super.onDestroy();
    }

    // Methode zum Verbergen der Tastatur
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void deleteLearningEffortButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_deletion_title);
        builder.setMessage(R.string.alert_dialog_deletion_question_learning_effort);

        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteLearningEffort();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}