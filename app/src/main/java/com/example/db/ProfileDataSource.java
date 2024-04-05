package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.psp.Profile;

public class ProfileDataSource {

        private SQLiteDatabase db;
        private final MyDatabaseHelper myDatabaseHelper;
        private Cursor cursor;

    public ProfileDataSource(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
    }


        // Tabellen und Spaltennamen für die Profile-Tabelle
        static final String tableProfile = "profile";
        static final String columnProfileID = "profile_id";
        static final String columnFirstName = "first_name";
        static final String columnLastName = "last_name";
        static final String columnStudyProgram = "study_program";

        // SQL-Befehl zum anlegen der Tabelle als String
        public static final String createTableProfile = "CREATE TABLE " + tableProfile + "("
                + columnProfileID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + columnFirstName + " TEXT,"
                + columnLastName + " TEXT,"
                + columnStudyProgram + " TEXT" + ")";


    public void addProfile(String firstName, String lastName, String studyProgram) {
        db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnFirstName, firstName);
        values.put(columnLastName, lastName);
        values.put(columnStudyProgram, studyProgram);
        db.insert(tableProfile, null, values);
        close();
    }

    private Profile cursorToProfile(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            int profileIDIndex = cursor.getColumnIndexOrThrow(columnProfileID);
            int firstNameIndex = cursor.getColumnIndex(columnFirstName);
            int lastNameIndex = cursor.getColumnIndex(columnLastName);
            int studyProgramIndex = cursor.getColumnIndex(columnStudyProgram);

            // cursor.getString verlangt, dass geprüft wird, dass der Index nicht -1 entspricht.
            // Stattdessen könnte man den Index auch statisch setzen, da nur ein Profil existieren sollte, jedoch wäre dies eine unschöne Lösung.
            if (profileIDIndex == -1 || firstNameIndex == -1 || lastNameIndex == -1 || studyProgramIndex == -1) {
                return null;
            }

            int profileID = cursor.getInt(profileIDIndex);
            String firstName = cursor.getString(firstNameIndex);
            String lastName = cursor.getString(lastNameIndex);
            String studyProgram = cursor.getString(studyProgramIndex);

            return new Profile(profileID, firstName, lastName, studyProgram);
        }
        return null;
    }

    public void updateProfile(Profile profile) {
        db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnFirstName, profile.getFirstName());
        values.put(columnLastName, profile.getLastName());
        values.put(columnStudyProgram, profile.getStudyProgram());

        db.update(tableProfile, values, columnProfileID + " = ?", new String[] {Integer.toString(profile.getProfileID())});
        close();
    }

    public Profile getProfile() {
        Profile profile;

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableProfile,null);

        profile = cursorToProfile(cursor);
        close();
        return profile;
    }

    public Profile getProfile(int profileId) {
        Profile profile;

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableProfile + " WHERE " + columnProfileID +" = " + profileId,null);

        profile = cursorToProfile(cursor);
        close();
        return profile;
    }

    public void close() {
        if(cursor != null) {
            cursor.close();
        }

        if(db != null) {
            db.close();
        }

        myDatabaseHelper.close();
    }
}

