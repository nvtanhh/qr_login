package com.example.qrlogin.models;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    public String uid;

    @SerializedName("email")
    public String email;
    @SerializedName("fullName")
    public String fullName;
    @SerializedName("studentId")
    public String studentId;
    @SerializedName("gender")
    public String gender;
    @SerializedName("gpa")
    public float gpa;


    public User() {
    }

    public User(String uid, String email, String fullName, String studentId, String gender, float gpa) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.studentId = studentId;
        this.gender = gender;
        this.gpa = gpa;
    }

    public User(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.fullName = null;
        this.studentId = null;
        this.gender = null;
        this.gpa = -1;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("fullName", fullName);
        result.put("studentId", studentId);
        result.put("gender", gender);
        result.put("gpa", gpa != -1 ? gpa : null);
        return result;
    }


    @NotNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", studentID='" + studentId + '\'' +
                ", gender='" + gender + '\'' +
                ", gpa=" + gpa +
                '}';
    }

    public String getUid() {
        return uid;
    }
}
