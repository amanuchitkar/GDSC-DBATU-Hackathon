package com.example.psp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.CourseDataSource;

import java.util.ArrayList;

public class NewEditCourse extends AppCompatActivity {

    private CourseDataSource courseDataSource;

    private String mode;
    private com.example.psp.Course course;
    private Profile profile;

    private EditText courseTitleEditText;
    private EditText courseDescriptionEditText;
    private Spinner semesterSpinner;

    private ArrayAdapter<String> adapter;
    private String courseTitle;
    private String courseDescription;
    private int courseSemester;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_course);

        courseDataSource = new CourseDataSource(this);

        mode = getIntent().getStringExtra("MODE");
        profile = (Profile) getIntent().getSerializableExtra("PROFILE_OBJECT");

        courseTitleEditText = findViewById(R.id.courseTitleEditText);
        courseDescriptionEditText = findViewById(R.id.courseDescriptionEditText);
        semesterSpinner = findViewById(R.id.semesterSpinner);

        initializeSpinnerValues();

        if (savedInstanceState != null) {
            profile = (Profile) savedInstanceState.getSerializable("PROFILE_OBJECT");
            course = (com.example.psp.Course) savedInstanceState.getSerializable("COURSE_OBJECT");
        }

        findViewById(R.id.NewEditCourseConstraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return true;
            }
        });

        if(mode.equals("EDIT")) {
            Button newEditCourseButton = findViewById(R.id.newEditCourseButton);
            newEditCourseButton.setText(R.string.save_course);

            if (course == null) {
                course = (com.example.psp.Course) getIntent().getSerializableExtra("COURSE_OBJECT");
            }

            int indexOfCurrentSemester = adapter.getPosition(String.valueOf(course.getCourseSemester()));

            courseTitleEditText.setText(course.getCourseTitle());
            courseDescriptionEditText.setText(course.getCourseDescription());
            semesterSpinner.setSelection(indexOfCurrentSemester);
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("PROFILE_OBJECT", profile);
        outState.putSerializable("COURSE_OBJECT", course);
    }

    private void initializeSpinnerValues() {
        ArrayList<String> values = new ArrayList<>();
        values.add("");

        for(int i = 1; i <= 8; i++) {
            values.add(String.valueOf(i));
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values);
        semesterSpinner.setAdapter(adapter);
    }

    public void saveCourseButtonClicked(View view) {

        if(areFieldsEmpty()) {
            String message = getResources().getString(R.string.toast_error_edittexts_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            courseTitle = courseTitleEditText.getText().toString().trim();
            courseDescription = courseDescriptionEditText.getText().toString().trim();
            courseSemester = Integer.parseInt(semesterSpinner.getSelectedItem().toString().trim());

            if(mode.equals("NEW")) {
                newSaveCourse();
            } else if(mode.equals("EDIT")) {
                editSaveCourse();
            }
        }
    }

    private void newSaveCourse() {

        courseDataSource.addCourse(courseTitle, courseDescription, courseSemester, profile.getProfileID());
        String message = getResources().getString(R.string.toast_new_course);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CourseList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("PROFILE_OBJECT", profile);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    private void editSaveCourse() {

        course.setCourseTitle(courseTitle);
        course.setCourseDescription(courseDescription);
        course.setCourseSemester(courseSemester);

        courseDataSource.updateCourse(course);
        String message = getResources().getString(R.string.toast_edit_course);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CourseDetails.class);
        intent.putExtra("COURSE_OBJECT", course);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }


    private boolean areFieldsEmpty() {
        return TextUtils.isEmpty(courseTitleEditText.getText().toString().trim())
                || TextUtils.isEmpty(courseDescriptionEditText.getText().toString().trim())
                || TextUtils.isEmpty(semesterSpinner.getSelectedItem().toString().trim());
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }
    @Override
    protected void onDestroy() {
        courseDataSource.close();
        super.onDestroy();
    }

    // Methode zum Verbergen der Tastatur
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}