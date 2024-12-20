package com.example.studentinformationsystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentDB";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_STUDENTS = "students";

    // Common column names
    private static final String KEY_STUDENT_ID = "student_id";

    // Users Table Columns
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Students Table Columns
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_DEPARTMENT = "department";
    private static final String KEY_PROFILE_PHOTO = "profile_photo";

    // Create Users Table
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_USERNAME + " TEXT PRIMARY KEY,"
            + KEY_PASSWORD + " TEXT,"
            + KEY_STUDENT_ID + " TEXT UNIQUE"
            + ")";

    // Create Students Table
    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_STUDENTS + "("
            + KEY_STUDENT_ID + " TEXT PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_DEPARTMENT + " TEXT,"
            + KEY_PROFILE_PHOTO + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // Add new user
    public boolean addUser(String username, String password, String studentId) {
        if (username == null || password == null || studentId == null) {
            return false;
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, username.trim());
            values.put(KEY_PASSWORD, password.trim());
            values.put(KEY_STUDENT_ID, studentId.trim());

            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check user credentials
    public boolean checkUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] columns = {KEY_USERNAME};
            String selection = KEY_USERNAME + " = ? AND " + KEY_PASSWORD + " = ?";
            String[] selectionArgs = {username.trim(), password.trim()};

            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);
            int count = cursor.getCount();
            cursor.close();
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get student ID based on username
    public String getStudentId(String username) {
        if (username == null) {
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] columns = {KEY_STUDENT_ID};
            String selection = KEY_USERNAME + " = ?";
            String[] selectionArgs = {username.trim()};

            Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);
            String studentId = null;
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(KEY_STUDENT_ID);
                if (columnIndex != -1) {
                    studentId = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return studentId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add new student
    public boolean addStudent(Student student) {
        if (student == null || student.getStudentId() == null) {
            return false;
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_STUDENT_ID, student.getStudentId().trim());
            values.put(KEY_NAME, student.getName() != null ? student.getName().trim() : "");
            values.put(KEY_EMAIL, student.getEmail() != null ? student.getEmail().trim() : "");
            values.put(KEY_PHONE, student.getPhone() != null ? student.getPhone().trim() : "");
            values.put(KEY_DEPARTMENT, student.getDepartment() != null ? student.getDepartment().trim() : "");
            values.put(KEY_PROFILE_PHOTO, student.getProfilePhoto());

            long result = db.insert(TABLE_STUDENTS, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get student details
    public Student getStudent(String studentId) {
        if (studentId == null) {
            return null;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String[] columns = {KEY_STUDENT_ID, KEY_NAME, KEY_EMAIL, KEY_PHONE,
                    KEY_DEPARTMENT, KEY_PROFILE_PHOTO};
            String selection = KEY_STUDENT_ID + " = ?";
            String[] selectionArgs = {studentId.trim()};

            Cursor cursor = db.query(TABLE_STUDENTS, columns, selection, selectionArgs,
                    null, null, null);
            Student student = null;
            if (cursor != null && cursor.moveToFirst()) {
                student = new Student(
                        getSafeString(cursor, KEY_STUDENT_ID),
                        getSafeString(cursor, KEY_NAME),
                        getSafeString(cursor, KEY_EMAIL),
                        getSafeString(cursor, KEY_PHONE),
                        getSafeString(cursor, KEY_DEPARTMENT),
                        getSafeString(cursor, KEY_PROFILE_PHOTO)
                );
                cursor.close();
            }

            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSafeString(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return columnIndex != -1 ? cursor.getString(columnIndex) : null;
    }

    // Update student details
    public boolean updateStudent(Student student) {
        if (student == null || student.getStudentId() == null) {
            return false; // Return false if student object or student ID is null
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add updated values to ContentValues with null checks
        values.put(KEY_NAME, student.getName() != null ? student.getName().trim() : "");
        values.put(KEY_EMAIL, student.getEmail() != null ? student.getEmail().trim() : "");
        values.put(KEY_PHONE, student.getPhone() != null ? student.getPhone().trim() : "");
        values.put(KEY_DEPARTMENT, student.getDepartment() != null ? student.getDepartment().trim() : "");
        values.put(KEY_PROFILE_PHOTO, student.getProfilePhoto());

        try {
            // Update the row in the 'students' table where student_id matches
            int rowsAffected = db.update(
                    TABLE_STUDENTS,
                    values,
                    KEY_STUDENT_ID + " = ?",
                    new String[]{student.getStudentId().trim()}
            );

            // Return true if at least one row was updated
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
