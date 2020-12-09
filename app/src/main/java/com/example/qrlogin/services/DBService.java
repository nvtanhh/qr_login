package com.example.qrlogin.services;

import android.util.Log;

import com.example.qrlogin.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class DBService {
    public static final String TAG = "DB_Service";

    private final String projectId = "qr-code-login-system";
    private DatabaseReference mDatabase;

    public DBService() throws IOException {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public void writeUser(User user) {
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user.toMap());
    }

    public void updateUser(User user) {
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(user.toMap());
    }


}
