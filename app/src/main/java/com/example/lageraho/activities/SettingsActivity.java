package com.example.lageraho.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.lageraho.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID, downloadUrl;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth; // created a variable for firebase authentication
    private DatabaseReference rootDatabaseReference; // created a for firebase Database reference
    private StorageReference profileImagereference;

    private static final int GALLERY_PICK_UP = 1;

    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(this);

        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();  // return the current userId
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference(); // provide the fireBase database reference
        profileImagereference = FirebaseStorage.getInstance().getReference().child("Profile Images");

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

        // this method retrieve user info. from database and display it in the Settings Activity
        retrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open the Gallery to selected Image for upload...
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_UP);

            }
        });
    }

    private void retrieveUserInfo() {

        // move to the current Login user ID and extract its details...
        rootDatabaseReference.child("Users").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name") && (dataSnapshot.hasChild("Image")))) {

                            // retrieve user name of current user ID
                            String retrieveUserName = dataSnapshot.child("Name").getValue().toString();

                            // retrieve user status of current user ID
                            String retrieveUserStatus = dataSnapshot.child("Status").getValue().toString();

                            // retrieve user image of current user ID
                            String retrieveUserImage = dataSnapshot.child("Image").getValue().toString();

                            userName.setEnabled(false);
                            userStatus.setEnabled(false);
                            userName.setText(retrieveUserName);  // set the userName of current User logged In.
                            userStatus.setText(retrieveUserStatus);  // set the status of current User logged In.

                            Picasso.get().load(retrieveUserImage).into(userProfileImage); // set the userImage of current log in user...

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Name"))) {

                            // retrieve user name of current user ID
                            String retrieveUserName = dataSnapshot.child("Name").getValue().toString();

                            // retrieve user status of current user ID
                            String retrieveUserStatus = dataSnapshot.child("Status").getValue().toString();

                            userStatus.setEnabled(false);
                            userName.setEnabled(false);
                            userName.setText(retrieveUserName);  // set the userName of current User logged In.
                            userStatus.setText(retrieveUserStatus);  // set the status of current User logged In.

                        } else {
                            Toast.makeText(SettingsActivity.this, "Please Update your Profile first.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateSettings() {

        // the username and user status for updating it...
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

            // used HashMap data structure to store the info in Key - Value pair
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uID", currentUserID);
            profileMap.put("Name", setUserName);
            profileMap.put("Status", setUserStatus);

            // set the details of each user in correct user ID
            rootDatabaseReference.child("Users").child(currentUserID).updateChildren(profileMap)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_UP && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Set Profile Image");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                final StorageReference filePathReference = profileImagereference.child(currentUserID + ".jpg");

                final UploadTask uploadTask = filePathReference.putFile(resultUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(SettingsActivity.this, "Profile Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    throw task.getException();
                                }

                                downloadUrl = filePathReference.getDownloadUrl().toString();
                                return filePathReference.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadUrl = task.getResult().toString();

                                    rootDatabaseReference.child("Users").child(currentUserID).child("Image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Profile Image URL saved!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String errorMessage = task.getException().toString();
                                                progressDialog.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

            }
        }
    }
}
