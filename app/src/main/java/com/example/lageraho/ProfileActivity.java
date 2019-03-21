package com.example.lageraho;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton;

    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // create a fireBase database reference to the Users section...
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        // get the user ID of selected user from FindFriendActivity and pass it in this activity...
        receiverUserID = getIntent().getExtras().get("visit_user_ID").toString();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_profile_username);
        userProfileStatus = findViewById(R.id.visit_profile_status);

        sendMessageRequestButton = findViewById(R.id.send_message_request_button);

        retrieveUserInformation();

    }

    private void retrieveUserInformation() {

        userReference.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // if all the three fields exist then show it...
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Image"))){

                    String userImage = dataSnapshot.child("Image").getValue().toString();
                    String userName = dataSnapshot.child("Name").getValue().toString();
                    String userStatus = dataSnapshot.child("Status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                }
                // if only username and userStatus exist then display it
                else {
                    String userName = dataSnapshot.child("Name").getValue().toString();
                    String userStatus = dataSnapshot.child("Status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
