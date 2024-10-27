package com.example.sprintproject.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VacationTimeRepository extends SQLiteOpenHelper {
    private static VacationTimeRepository instance;
    private static final String DATABASE_NAME = "vacation_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_VACATION_TIME = "vacation_time";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";

    private VacationTimeRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized VacationTimeRepository getInstance(Context context) {
        if (instance == null) {
            instance = new VacationTimeRepository(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_VACATION_TIME_TABLE = "CREATE TABLE " + TABLE_VACATION_TIME + "("
                + KEY_USER_ID + " TEXT,"
                + KEY_DURATION + " INTEGER,"
                + KEY_START_DATE + " TEXT,"
                + KEY_END_DATE + " TEXT,"
                + "PRIMARY KEY (" + KEY_USER_ID + "))";
        db.execSQL(CREATE_VACATION_TIME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VACATION_TIME);
        onCreate(db);
    }

    public void saveVacationTime(VacationTime vacationTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, vacationTime.getUserId());
        values.put(KEY_DURATION, vacationTime.getDuration());
        values.put(KEY_START_DATE, vacationTime.getStartDate());
        values.put(KEY_END_DATE, vacationTime.getEndDate());

        db.insertWithOnConflict(TABLE_VACATION_TIME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public VacationTime getVacationTime(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_VACATION_TIME, null,
                KEY_USER_ID + "=?", new String[]{userId},
                null, null, null);

        VacationTime vacationTime = null;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    vacationTime = new VacationTime(
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_USER_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DURATION)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_DATE))
                    );
                }
            } catch (IllegalArgumentException e) {
                // Handle exception if needed
            } finally {
                cursor.close();
            }
        }
        return vacationTime;
    }
}