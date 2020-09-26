package com.example.lageraho.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lageraho.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button createAccountButton;
    private TextView alreadyHaveAccount;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        createAccountButton = findViewById(R.id.register_button);
        alreadyHaveAccount = findViewById(R.id.already_have_an_account_link);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            userEmail.setError("Email is required");
            userEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)){
            userPassword.setError("Password is required");
            userPassword.requestFocus();
            return;
        }
        if (password.length() < 8){
            Toast.makeText(RegisterActivity.this, "Password length should be more than 8 characters", Toast.LENGTH_SHORT).show();
        }
        else {

            progressDialog.setTitle("Sign Up");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String currentUserId = firebaseAuth.getCurrentUser().getUid();  // get the ID of current User i.e. New User
                        rootRef.child("Users").child(currentUserId).setValue("");  // set the ID in FireBase Database in Users

                        rootRef.child("Users").child(currentUserId).child("Device_Token")
                                .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    progressDialog.dismiss();
                                    sendUserToMainActivity();
                                    Toast.makeText(RegisterActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        progressDialog.dismiss();
                        String errorMessage = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}