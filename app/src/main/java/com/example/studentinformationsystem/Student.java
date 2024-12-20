package com.example.studentinformationsystem;

public class Student {
    private String studentId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String profilePhoto;

    public Student(String studentId, String name, String email, String phone,
                   String department, String profilePhoto) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.profilePhoto = profilePhoto;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getDepartment() { return department; }
    public String getProfilePhoto() { return profilePhoto; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDepartment(String department) { this.department = department; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
}