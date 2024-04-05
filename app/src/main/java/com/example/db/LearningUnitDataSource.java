package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.psp.LearningUnit;
import com.example.psp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LearningUnitDataSource {

    private SQLiteDatabase db;
    private final MyDatabaseHelper myDatabaseHelper;
    private Cursor cursor;

    private final SimpleDateFormat sdf;

    public LearningUnitDataSource(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        sdf = new SimpleDateFormat(context.getString(R.string.sdf_standard_format_date));
    }

    // Tabellen und Spaltennamen für die LearningUnit-Tabelle
    static final String tableLearningUnit = "learning_unit";
    static final String columnLearningUnitId = "learning_unit_id";
    static final String columnTitle = "title";
    static final String columnPlannedLearningEffort = "planned_learning_effort";
    static final String columnCreationDate = "creation_datetime";

    // SQL-Befehl zum anlegen der Tabelle als String
    public static final String createTableLearningUnit = "CREATE TABLE " + tableLearningUnit + "("
            + columnLearningUnitId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + columnTitle + " TEXT,"
            + columnPlannedLearningEffort + " INTEGER,"
            + columnCreationDate + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + CourseDataSource.columnCourseId + " INTEGER,"
            + "FOREIGN KEY(" + CourseDataSource.columnCourseId + ") REFERENCES "
            + CourseDataSource.tableCourse + "(" + CourseDataSource.columnCourseId + ")"
            + " ON DELETE CASCADE" + ")";

    public void addLearningUnit(String title, int plannedLearningEffortHours, int plannedLearningEffortMinutes, int courseId) {
        db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnTitle, title);
        values.put(columnPlannedLearningEffort, LearningUnit.calculateLearningEffort(plannedLearningEffortHours, plannedLearningEffortMinutes));
        values.put(CourseDataSource.columnCourseId, courseId);
        db.insert(tableLearningUnit, null, values);
        close();
    }

    public ArrayList<LearningUnit> getLearningUnitsForCourse(int courseId) {
        ArrayList<LearningUnit> learningUnits = new ArrayList<>();

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableLearningUnit + " WHERE " + CourseDataSource.columnCourseId +" = " + courseId,null);

        if (cursor.moveToFirst()) {
            do {
                LearningUnit learningUnit = cursorToLearningUnit(cursor);
                learningUnits.add(learningUnit);
            } while (cursor.moveToNext());
        }
        close();
        return learningUnits;
    }

    public LearningUnit getLearningUnit(int learningUnitId) {
        LearningUnit learningUnit;

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableLearningUnit + " WHERE " + columnLearningUnitId +" = " + learningUnitId,null);

        learningUnit = cursorToLearningUnit(cursor);
        close();
        return learningUnit;
    }

    private LearningUnit cursorToLearningUnit(Cursor cursor) {
        if (cursor != null) {

            int learningUnitIDIndex = cursor.getColumnIndexOrThrow(columnLearningUnitId);
            int creationDateIndex = cursor.getColumnIndex(columnCreationDate);
            int titleIndex = cursor.getColumnIndex(columnTitle);
            int plannedLearningEffortIndex = cursor.getColumnIndex(columnPlannedLearningEffort);

            int learningUnitId = cursor.getInt(learningUnitIDIndex);
            String creationDateString = cursor.getString(creationDateIndex);
            String title = cursor.getString(titleIndex);
            int plannedLearningEffort = cursor.getInt(plannedLearningEffortIndex);

            Date creationDate = null;

            try {
                creationDate = sdf.parse(creationDateString);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            return new LearningUnit(learningUnitId, creationDate, title, plannedLearningEffort);
        }
        return null;
    }

    public void updateLearningUnit(LearningUnit learningUnit) {
        db = myDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(columnTitle, learningUnit.getLearningUnitTitle());
        values.put(columnPlannedLearningEffort, learningUnit.getPlannedLearningEffort());

        db.update(tableLearningUnit, values, columnLearningUnitId + " = ?", new String[] {Integer.toString(learningUnit.getLearningUnitId())});
        close();
    }

    public void removeLearningUnit(LearningUnit learningUnit) {
        db = myDatabaseHelper.getWritableDatabase();
        db.delete(tableLearningUnit, columnLearningUnitId + " = ?", new String[] {Integer.toString(learningUnit.getLearningUnitId())});
        close();
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
