package com.example.qrlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.qrlogin.models.User;
import com.example.qrlogin.services.DBService;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    User user;
    String uid;
    private Button signUpBtn;
    private TextInputLayout tfEmail, tfName, tfStudentId, tfGpa, tfGender;
    public static final String TAG = "Profile";
    private DatabaseReference mDatabase;
    LoadingDialog loadingDialog;
    private View logOutBtn;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        loadingDialog = new LoadingDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getUser();

        initView();
    }

    private void initView() {

        updateBtn = findViewById(R.id.update);
        updateBtn.setOnClickListener(this);

        logOutBtn = findViewById(R.id.logout);
        logOutBtn.setOnClickListener(this);

        tfEmail = findViewById(R.id.email_tf);
        tfName = findViewById(R.id.name_tf);
        tfStudentId = findViewById(R.id.studentID_tf);
        tfGpa = findViewById(R.id.gpa_tf);
        tfGender = findViewById(R.id.gender_tf);
        if (user != null) {
            tfEmail.getEditText().setText(user.email != null ? user.email : "");
            tfName.getEditText().setText(user.fullName != null ? user.fullName : "");
            tfStudentId.getEditText().setText(user.studentId != null ? user.studentId : "");
            tfGpa.getEditText().setText(user.gpa != 0.0 ? Float.toString(user.gpa) : "");
            initDropdownMenu();
        }


    }

    private void initDropdownMenu() {
        final String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        ProfileActivity.this,
                        R.layout.dropdown_menu_popup_item,
                        genders);
        AutoCompleteTextView autoCompleteTextView =
                findViewById(R.id.gender_dropdown);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(user.gender != null ? user.gender : "", false);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                Log.i("SELECTED TEXT WAS: ", genders[position]);
                user.gender = selection;
            }
        });
    }

    private void getUser() {
        showLoadingDialog();
        uid = (String) getIntent().getSerializableExtra("id");
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue().toString());
                User userFetched = new Gson().fromJson(snapshot.getValue().toString(), User.class);
                updateUI(userFetched);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showLoadingDialog() {
        loadingDialog.showLoadingDialog();
    }

    private void hideLoadingDialog() {
        loadingDialog.hideLoadingDialog();
    }

    private void updateUI(User userFetched) {
        user = userFetched;
        initView();
        hideLoadingDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                showSignOutDialog();
                break;
            case R.id.update:
//                update api
                user.fullName = tfName.getEditText().getText().toString();
                user.studentId = tfStudentId.getEditText().getText().toString();
                try {
                    user.gpa = Float.valueOf(tfGpa.getEditText().getText().toString());
                    tfGpa.setError(null);
                } catch (Exception e) {
                    tfGpa.setError("Parse error!");
                }

                System.out.println("UPDATED USER: " + user);
                try {
                    DBService db = new DBService();
                    db.updateUser(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                update ui
                updateUI(user);
                Toast.makeText(ProfileActivity.this, "Profile updated!",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }


    void showSignOutDialog() {
        new AlertDialog.Builder(this).setTitle("Are you sure you want to logout?").setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }
}