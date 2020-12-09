package com.example.qrlogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.example.qrlogin.models.User;
import com.example.qrlogin.services.DBService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    private Button signUpBtn;
    private TextInputLayout tfEmail, tfPass, tfRePass;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        loadingDialog = new LoadingDialog(SignUpActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
        mAuth = FirebaseAuth.getInstance();
    }


    private void initView() {
        signUpBtn = findViewById(R.id.sign_up);
        signUpBtn.setOnClickListener(mMyListener);

        tfEmail = findViewById(R.id.email_tf);
        tfPass = findViewById(R.id.password_tf);
        tfRePass = findViewById(R.id.confirm_password_tf);

//        add text change listener future
    }

    public void register(final String email, final String password) {
        showProgressBar();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hideProgressBar();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "createUserWithEmail:success ==> UserID = " + user.getUid());

                            try {
                                DBService db = new DBService();
                                db.writeUser(new User(mAuth.getCurrentUser().getUid(), email));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Intent intent = new Intent(SignUpActivity.this, AfterSignUp.class);
                            String code = email + "\r\n" + password;
                            intent.putExtra("code", code);
                            startActivity(intent);

                        } else {
                            hideProgressBar();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void showProgressBar() {
        loadingDialog.showLoadingDialog();
    }

    private void hideProgressBar() {
        loadingDialog.hideLoadingDialog();
    }

    private boolean validateForm() {
        boolean isValid = true;
        String email = tfEmail.getEditText().getText().toString().trim();
        String pass = tfPass.getEditText().getText().toString().trim();
        String rePass = tfRePass.getEditText().getText().toString().trim();


        if (email.isEmpty()) {
            tfEmail.setError("Email is required");
            isValid = false;
        } else if (!validateEmail(email)) {
            tfEmail.setError("Email is invalid");
            isValid = false;
        }

        if (pass.isEmpty()) {
            tfPass.setError("Password is required");
            isValid = false;
        } else if (pass.length() < 6) {
            tfPass.setError("Password is at least 6 characters");
            isValid = false;
        }
        if (rePass.isEmpty()) {
            tfRePass.setError("Confirm password is required");
            isValid = false;
        } else if (rePass.length() < 6) {
            tfRePass.setError("Password is at least 6 characters");
            isValid = false;
        }
        if (!pass.equals(rePass)) {
            tfPass.setError("Password not matched");
            tfRePass.setError("Password not matched");
            isValid = false;
        }
        if (isValid) {
            tfEmail.setError(null);
            tfPass.setError(null);
            tfRePass.setError(null);
        }
        return isValid;
    }

    private boolean validateEmail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }


    private View.OnClickListener mMyListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.sign_up && validateForm()) {
                register(tfEmail.getEditText().getText().toString(), tfPass.getEditText().getText().toString());
            }
        }
    };
}