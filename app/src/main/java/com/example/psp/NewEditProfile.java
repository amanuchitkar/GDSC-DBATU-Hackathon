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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.db.ProfileDataSource;

public class NewEditProfile extends AppCompatActivity {

    private ProfileDataSource profileDataSource;
    private String mode;

    private Profile profile;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText studyProgramEditText;
    private String firstName;
    private String lastName;
    private String studyProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_edit_profile);

        profileDataSource = new ProfileDataSource(this);

        mode = getIntent().getStringExtra("MODE");

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        studyProgramEditText = findViewById((R.id.studyProgramEditText));

        if (savedInstanceState != null) {
            profile = (Profile) savedInstanceState.getSerializable("PROFILE_OBJECT");
        }

        findViewById(R.id.EditNewProfileConstraintLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideKeyboard(view);
                return true;
            }
        });

    if(mode.equals("EDIT")) {

        Button newEditProfileButton = findViewById(R.id.newEditProfileButton);
        newEditProfileButton.setText(R.string.save_profile);

        if (profile == null) {
            profile = (Profile) getIntent().getSerializableExtra("PROFILE_OBJECT");
        }

        firstNameEditText.setText(profile.getFirstName());
        lastNameEditText.setText(profile.getLastName());
        studyProgramEditText.setText(profile.getStudyProgram());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("PROFILE_OBJECT", profile);
    }

    public void backButtonClicked(View view) {
        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
    }

public void saveProfileButtonClicked(View view) {

        if(areFieldsEmpty()) {
            String message = getResources().getString(R.string.toast_error_edittexts_empty);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
             firstName = firstNameEditText.getText().toString().trim();
             lastName = lastNameEditText.getText().toString().trim();
             studyProgram = studyProgramEditText.getText().toString().trim();

                if(mode.equals("NEW")) {
                    newSaveProfile();
                } else if(mode.equals("EDIT")) {
                    editSaveProfile();
                }
            }
}

private void newSaveProfile() {
    profileDataSource.addProfile(firstName, lastName, studyProgram);
    String message = getResources().getString(R.string.toast_new_profile);
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    Intent intent = new Intent(this, CourseList.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Nötig, damit nicht zurückgekehrt werden kann zu NewEditProfile und Welcome.
    startActivity(intent);
    finish();
    overridePendingTransition(R.anim.animation_slide_right_in, R.anim.animation_slide_left_in);
}

private void editSaveProfile() {

        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setStudyProgram(studyProgram);

        profileDataSource.updateProfile(profile);
        String message = getResources().getString(R.string.toast_edit_profile);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CourseList.class);
        intent.putExtra("PROFILE_OBJECT", profile);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.animation_slide_right_out, R.anim.animation_slide_left_out);
}

private boolean areFieldsEmpty() {
        return TextUtils.isEmpty(firstNameEditText.getText().toString().trim())
                || TextUtils.isEmpty(lastNameEditText.getText().toString().trim())
                || TextUtils.isEmpty(studyProgramEditText.getText().toString().trim());
    }

    protected void onDestroy() {
        profileDataSource.close();
        super.onDestroy();
    }

    // Methode zum Verbergen der Tastatur
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}