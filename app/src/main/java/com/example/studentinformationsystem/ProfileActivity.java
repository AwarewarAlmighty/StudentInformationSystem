package com.example.studentinformationsystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ProfileActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private ImageView ivProfilePhoto;
    private EditText etName, etEmail, etPhone, etDepartment;
    private Button btnTakePhoto, btnUpdate;
    private DatabaseHelper databaseHelper;
    private SharedPrefUtil sharedPrefUtil;
    private Student currentStudent;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize utilities
        databaseHelper = new DatabaseHelper(this);
        sharedPrefUtil = new SharedPrefUtil(this);

        // Initialize views
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etDepartment = findViewById(R.id.etDepartment);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Load current student data
        loadStudentData();

        // Take photo button click handler
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        // Update button click handler
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void loadStudentData() {
        String username = sharedPrefUtil.getUsername();
        String studentId = databaseHelper.getStudentId(username);
        currentStudent = databaseHelper.getStudent(studentId);

        if (currentStudent != null) {
            etName.setText(currentStudent.getName());
            etEmail.setText(currentStudent.getEmail());
            etPhone.setText(currentStudent.getPhone());
            etDepartment.setText(currentStudent.getDepartment());

            // Load profile photo if exists
            if (currentStudent.getProfilePhoto() != null && !currentStudent.getProfilePhoto().isEmpty()) {
                // Load photo using appropriate method
            }
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivProfilePhoto.setImageBitmap(imageBitmap);
            // Save image path or bitmap
            saveProfilePhoto(imageBitmap);
        }
    }

    private void saveProfilePhoto(Bitmap photo) {
        // Implement photo saving logic (to internal storage or convert to Base64)
        // Update currentPhotoPath
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();

        if (validateInput(name, email, phone, department)) {
            if (currentStudent != null) {
                currentStudent.setName(name);
                currentStudent.setEmail(email);
                currentStudent.setPhone(phone);
                currentStudent.setDepartment(department);
                currentStudent.setProfilePhoto(currentPhotoPath);

                if (databaseHelper.updateStudent(currentStudent)) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String name, String email, String phone, String department) {
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || department.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}