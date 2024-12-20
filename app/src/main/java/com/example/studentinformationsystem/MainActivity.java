package com.example.studentinformationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.studentinformationsystem.R;

public class MainActivity extends AppCompatActivity {
    private TextView tvName, tvStudentId, tvEmail, tvDepartment;
    private ImageView ivProfile;
    private DatabaseHelper databaseHelper;
    private SharedPrefUtil sharedPrefUtil;
    private Student currentStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize utilities
        databaseHelper = new DatabaseHelper(this);
        sharedPrefUtil = new SharedPrefUtil(this);

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvEmail = findViewById(R.id.tvEmail);
        tvDepartment = findViewById(R.id.tvDepartment);
        ivProfile = findViewById(R.id.ivProfile);

        // Load student data
        loadStudentData();

        // Profile image click handler
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData();
    }

    private void loadStudentData() {
        String username = sharedPrefUtil.getUsername();
        String studentId = databaseHelper.getStudentId(username);
        currentStudent = databaseHelper.getStudent(studentId);

        if (currentStudent != null) {
            tvName.setText(currentStudent.getName());
            tvStudentId.setText(currentStudent.getStudentId());
            tvEmail.setText(currentStudent.getEmail());
            tvDepartment.setText(currentStudent.getDepartment());

            // Load profile photo if exists
            if (currentStudent.getProfilePhoto() != null && !currentStudent.getProfilePhoto().isEmpty()) {
                // Load photo using appropriate method (e.g., Bitmap, Glide, etc.)
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sharedPrefUtil.clearSession();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}