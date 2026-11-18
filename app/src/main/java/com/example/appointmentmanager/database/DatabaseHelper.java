package com.example.appointmentmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.appointmentmanager.model.Appointment;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String COLUMN_APT_ID = "id";
    public static final String COLUMN_APT_TITLE = "title";
    public static final String COLUMN_APT_LOCATION = "location";
    public static final String COLUMN_APT_DATE = "date";
    public static final String COLUMN_APT_TIME = "time";
    public static final String COLUMN_APT_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
                + COLUMN_APT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APT_TITLE + " TEXT NOT NULL, "
                + COLUMN_APT_LOCATION + " TEXT NOT NULL, "
                + COLUMN_APT_DATE + " TEXT NOT NULL, "
                + COLUMN_APT_TIME + " TEXT NOT NULL, "
                + COLUMN_APT_REMINDER + " INTEGER NOT NULL)";
        db.execSQL(CREATE_APPOINTMENTS_TABLE);

        // Insert sample user: admin / 123456
        ContentValues userValues = new ContentValues();
        userValues.put(COLUMN_USERNAME, "admin");
        userValues.put(COLUMN_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Insert 3 sample appointments
        // 1. Họp nhóm đồ án | Phòng B203 | 25/10/2026 (2026-10-25) | 09:00 | Có (1)
        insertSampleAppointment(db, "Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", 1);
        // 2. Khám răng | Nha khoa Minh Tâm | 26/10/2026 (2026-10-26) | 14:30 | Có (1)
        insertSampleAppointment(db, "Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", 1);
        // 3. Đi cà phê với bạn | Highland Coffee | 27/10/2026 (2026-10-27) | 19:00 | Không (0)
        insertSampleAppointment(db, "Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", 0);
    }

    private void insertSampleAppointment(SQLiteDatabase db, String title, String location, String date, String time, int reminder) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APT_TITLE, title);
        values.put(COLUMN_APT_LOCATION, location);
        values.put(COLUMN_APT_DATE, date);
        values.put(COLUMN_APT_TIME, time);
        values.put(COLUMN_APT_REMINDER, reminder);
        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null);
            exists = (cursor != null && cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long id = -1;
        try {
            id = db.insert(TABLE_USERS, null, values);
        } finally {
            db.close();
        }
        return id;
    }

    public long insertAppointment(Appointment appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APT_TITLE, appointment.getTitle());
        values.put(COLUMN_APT_LOCATION, appointment.getLocation());
        values.put(COLUMN_APT_DATE, appointment.getDate());
        values.put(COLUMN_APT_TIME, appointment.getTime());
        values.put(COLUMN_APT_REMINDER, appointment.getReminder());
        long id = -1;
        try {
            id = db.insert(TABLE_APPOINTMENTS, null, values);
        } finally {
            db.close();
        }
        return id;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_APPOINTMENTS,
                    null, null, null, null, null,
                    COLUMN_APT_DATE + " ASC, " + COLUMN_APT_TIME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APT_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_TITLE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_LOCATION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_TIME));
                    int reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APT_REMINDER));

                    Appointment apt = new Appointment(id, title, location, date, time, reminder);
                    list.add(apt);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return list;
    }

    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Appointment apt = null;
        try {
            cursor = db.query(TABLE_APPOINTMENTS,
                    null,
                    COLUMN_APT_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_LOCATION));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APT_TIME));
                int reminder = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APT_REMINDER));

                apt = new Appointment(id, title, location, date, time, reminder);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return apt;
    }

    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APT_REMINDER, on ? 1 : 0);
        int rows = 0;
        try {
            rows = db.update(TABLE_APPOINTMENTS, values, COLUMN_APT_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
        return rows;
    }

    public int deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = 0;
        try {
            rows = db.delete(TABLE_APPOINTMENTS, COLUMN_APT_ID + " = ?", new String[]{String.valueOf(id)});
        } finally {
            db.close();
        }
        return rows;
    }

    public int countAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }

    public int countReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPOINTMENTS + " WHERE " + COLUMN_APT_REMINDER + " = 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }
}
