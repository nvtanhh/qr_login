package com.example.qrlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, AfterSignUp.class);
        String code = "kingtxx98@gmail.com" + "\r\n" + "tanh1998";
        intent.putExtra("code", code);
        startActivity(intent);

//        setupButton();
    }

    private void setupButton() {
        Button loginBtn = findViewById(R.id.login);
        Button signUpBtn = findViewById(R.id.sign_up);

        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                break;
            case R.id.sign_up:
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                break;
        }
    }
}