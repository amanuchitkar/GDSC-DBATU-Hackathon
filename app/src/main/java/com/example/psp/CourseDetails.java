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

import com.example.adapter.LearningUnitAdapter;
import com.example.db.CourseDataSource;
import com.example.db.LearningUnitDataSource;

import java.util.ArrayList;

public class CourseDetails extends AppCompatActivity {


    private LearningUnitDataSource learningUnitDataSource;

    LearningUnitAdapter learningUnitAdapter;

    com.example.psp.Course course;
    private ArrayList<com.example.psp.LearningUnit> learningUnits;

    private boolean deletionConfirmed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        learningUnitDataSource = new LearningUnitDataSource(this);

        if (savedInstanceState != null) {
            course = (com.example.psp.Course) savedInstanceState.getSerializable("COURSE_OBJECT");

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("COURSE_OBJECT", course);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (course == null) {
            course = (com.example.psp.Course) getIntent().getSerializableExtra("COURSE_OBJECT");
        }

        setCourseTextViews();

        learningUnits = learningUnitDataSource.getLearningUnitsForCourse(course.getCourseId());
        initializeRecyclerView();

    }
    private void setCourseTextViews() {

        TextView courseTitleTextView = findViewById(R.id.courseTitleTextView);
        TextView courseDescriptionTextView = findViewById(R.id.courseDescriptionTextView);
        TextView courseSemesterTextView = findViewById(R.id.courseSemesterTextView);

        courseTitleTextView.setText(course.getCourseTitle());
        courseDescriptionTextView.setText(this.getString(R.string.description_dp, course.getCourseDescription()));
        courseSemesterTextView.setText(this.getString(R.string.semester_dp, course.getCourseSemester()));
    }

    public void changeCourseButtonClicked(View view) {
        Intent intent = new Intent(this, NewEditCourse.class);
        intent.putExtra("MODE", "EDIT");
        intent.putExtra("COURSE_OBJECT", course);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    public void deleteCourse () {

            CourseDataSource courseDataSource = new CourseDataSource(this);
            courseDataSource.removeCourse(course);

            String message = getResources().getString(R.string.toast_course_deleted);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            courseDataSource.close();
            finish();
            overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

    public void addLearningUnitButtonClicked(View view) {
        Intent intent = new Intent(this, NewEditLearningUnit.class);
        intent.putExtra("MODE", "NEW");
        intent.putExtra("COURSE_OBJECT", course);
        startActivity(intent);
        overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.courseDetailsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        learningUnitAdapter = new LearningUnitAdapter(learningUnits);
        recyclerView.setAdapter(learningUnitAdapter);
    }


    @Override
    protected void onDestroy() {

        learningUnitDataSource.close();
        super.onDestroy();
    }
    public void deleteCourseButtonClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_deletion_title);
        builder.setMessage(String.format(getString(R.string.alert_dialog_deletion_question_course), course.getCourseTitle()));

        builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCourse();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}