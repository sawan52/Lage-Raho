package com.example.lageraho;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        updateAccountSettings = findViewById(R.id.update_setting_button);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.profile_image);

        updateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        rootDatabaseReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name") && (dataSnapshot.hasChild("Image")))){

                            String retrieveUserName = dataSnapshot.child("Name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("Status").getValue().toString();
                            String retrieveUserImage = dataSnapshot.child("Image").getValue().toString();

                            userName.setEnabled(false);
                            userStatus.setEnabled(false);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name"))){

                            String retrieveUserName = dataSnapshot.child("Name").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("Status").getValue().toString();

                            userStatus.setEnabled(false);
                            userName.setEnabled(false);
                            userName.setText(retrieveUserName);
                            userStatus.setText(retrieveUserStatus);

                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "Please Update your Profile first.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateSettings() {

        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(SettingsActivity.this, "Please write your name first...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(SettingsActivity.this, "Please write your status first...", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setMessage("Updating...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uID", currentUserID);
            profileMap.put("Name", setUserName);
            profileMap.put("Status", setUserStatus);

            rootDatabaseReference.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();
                            } else {
                                progressDialog.dismiss();
                                String errorMessage = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
