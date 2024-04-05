package com.example.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.psp.R;
import com.example.psp.LearningEffort;
import com.example.psp.LearningUnit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LearningEffortDataSource {
    private SQLiteDatabase db;
    private final MyDatabaseHelper myDatabaseHelper;
    private Cursor cursor;

    private final SimpleDateFormat sdf;

    public LearningEffortDataSource(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        sdf = new SimpleDateFormat(context.getString(R.string.sdf_standard_format_date));
    }

    // Tabellen und Spaltennamen für die LearningUnit-Tabelle
    static final String tableLearningEffort = "learning_effort";
    static final String columnLearningEffortId = "learning_effort_id";
    static final String columnActualLearningEffort = "actual_learning_effort";
    static final String columnCreationDate = "creation_datetime";
    static final String columnLearningEffortDate = "learning_effort_date";

    // SQL-Befehl zum anlegen der Tabelle als String
    public static final String createTableLearningEffort= "CREATE TABLE " + tableLearningEffort + "("
            + columnLearningEffortId + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + columnCreationDate + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + columnLearningEffortDate + " DATETIME,"
            + columnActualLearningEffort + " INTEGER,"
            + LearningUnitDataSource.columnLearningUnitId + " INTEGER,"
            + "FOREIGN KEY(" + LearningUnitDataSource.columnLearningUnitId + ") REFERENCES "
            + LearningUnitDataSource.tableLearningUnit + "(" + LearningUnitDataSource.columnLearningUnitId + ")"
            + " ON DELETE CASCADE" + ")";

    public void addLearningEffort(Date learningEffortDate, int actualLearningEffortHours, int actualLearningEffortMinutes, int learningUnitId) {
        db = myDatabaseHelper.getWritableDatabase();

        String learningEffortDateString = sdf.format(learningEffortDate);

        ContentValues values = new ContentValues();
        values.put(columnLearningEffortDate, learningEffortDateString);
        values.put(columnActualLearningEffort, LearningUnit.calculateLearningEffort(actualLearningEffortHours, actualLearningEffortMinutes));
        values.put(LearningUnitDataSource.columnLearningUnitId, learningUnitId);
        db.insert(tableLearningEffort, null, values);
        close();
    }

    public ArrayList<LearningEffort> getLearningEffortsForLearningUnit(int learningUnitId) {
        ArrayList<LearningEffort> learningEfforts = new ArrayList<>();

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableLearningEffort + " WHERE " + LearningUnitDataSource.columnLearningUnitId +" = " + learningUnitId,null);

        if (cursor.moveToFirst()) {
            do {
                LearningEffort learningEffort = cursorToLearningEffort(cursor);
                learningEfforts.add(learningEffort);
            } while (cursor.moveToNext());
        }
        close();
        return learningEfforts;
    }

    public LearningEffort getLearningEffort(int learningEffortId) {
        LearningEffort learningEffort;

        db = myDatabaseHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + tableLearningEffort + " WHERE " + columnLearningEffortId +" = " + learningEffortId,null);

        learningEffort = cursorToLearningEffort(cursor);
        close();
        return learningEffort;
    }

    private LearningEffort cursorToLearningEffort(Cursor cursor) {
        if (cursor != null) {

            int learningEffortIDIndex = cursor.getColumnIndexOrThrow(columnLearningEffortId);
            int creationDateIndex = cursor.getColumnIndex(columnCreationDate);
            int learningEffortDateIndex = cursor.getColumnIndex(columnLearningEffortDate);
            int actualLearningEffortIndex = cursor.getColumnIndex(columnActualLearningEffort);

            int learningEffortId = cursor.getInt(learningEffortIDIndex);
            String creationDateString = cursor.getString(creationDateIndex);
            String learningEffortDateString = cursor.getString(learningEffortDateIndex);
            int actualLearningEffort = cursor.getInt(actualLearningEffortIndex);

            Date creationDate = null;
            Date learningEffortDate = null;

            try {
                creationDate = sdf.parse(creationDateString);
                learningEffortDate = sdf.parse(learningEffortDateString);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }

            return new LearningEffort(learningEffortId, creationDate, learningEffortDate, actualLearningEffort);
        }
        return null;
    }

    public void updateLearningEffort(LearningEffort learningEffort) {
        db = myDatabaseHelper.getWritableDatabase();

        String learningEffortDateString = sdf.format(learningEffort.getLearningEffortDate());

        ContentValues values = new ContentValues();
        values.put(columnLearningEffortDate, learningEffortDateString);
        values.put(columnActualLearningEffort, learningEffort.getActualLearningEffort());

        db.update(tableLearningEffort, values, columnLearningEffortId + " = ?", new String[] {Integer.toString(learningEffort.getLearningEffortId())});
        close();
    }

    public void removeLearningEffort(LearningEffort learningEffort) {
        db = myDatabaseHelper.getWritableDatabase();
        db.delete(tableLearningEffort, columnLearningEffortId + " = ?", new String[] {Integer.toString(learningEffort.getLearningEffortId())});
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
