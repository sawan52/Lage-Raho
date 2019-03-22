package com.example.lageraho;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, currentState, senderUserID;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button sendMessageRequestButton, declineMessageRequestButton;

    private DatabaseReference userReference, chatRequestReference, contactsReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        // create a fireBase database reference to the Users section...
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestReference = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
        senderUserID = firebaseAuth.getCurrentUser().getUid();

        // get the user ID of selected user from FindFriendActivity and pass it in this activity...
        receiverUserID = getIntent().getExtras().get("visit_user_ID").toString();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_profile_username);
        userProfileStatus = findViewById(R.id.visit_profile_status);

        sendMessageRequestButton = findViewById(R.id.send_message_request_button);
        declineMessageRequestButton = findViewById(R.id.decline_message_request_button);

        // initially make the current state of the User to New
        currentState = "New";

        // retrieve the user information and display it in the ProfileActivity
        retrieveUserInformation();

    }

    private void retrieveUserInformation() {

        // using database reference to retrieve information by logged In user selected users from FindFriendActivity
        userReference.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // if all the three fields exist then show it...
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Image"))) {

                    String userImage = dataSnapshot.child("Image").getValue().toString();
                    String userName = dataSnapshot.child("Name").getValue().toString();
                    String userStatus = dataSnapshot.child("Status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    // manages the chat Request
                    manageChatsRequest();

                }
                // if only username and userStatus exist then display it
                else {
                    String userName = dataSnapshot.child("Name").getValue().toString();
                    String userStatus = dataSnapshot.child("Status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    // manages the chat request
                    manageChatsRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void manageChatsRequest() {

        // using database reference the sender or current logged In user can do...
        chatRequestReference.child(senderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if receiver ID exists whom sender has send the request then,
                if (dataSnapshot.hasChild(receiverUserID)) {

                    // get that request Type from sender (means from Receiver ID)
                    String requestType = dataSnapshot.child(receiverUserID).child("Request Type").getValue().toString();

                    // check for the request type, if it equals to SENT
                    if (requestType.equals("Sent")) {

                        // then change the current state to REQUEST SENT & set the Text for sender Button to CANCEL CHAT REQUEST
                        currentState = "Request Sent";
                        sendMessageRequestButton.setText(R.string.cancel_chat_request);
                    }
                    // check for the request type, if it equals to RECEIVED
                    else if (requestType.equals("Received")) {

                        // then change the current state to REQUEST RECEIVED & set the Text for receiver Button to ACCEPT CHAT REQUEST also
                        // make VISIBLE the CANCEL REQUEST BUTTON from receiver side..
                        currentState = "Request Received";
                        sendMessageRequestButton.setText("Accept Chat Request");
                        declineMessageRequestButton.setVisibility(View.VISIBLE);
                        declineMessageRequestButton.setEnabled(true);

                        // add an onClickListener to perform some actions
                        declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // cancel the chat Request
                                cancelChatRequest();
                            }
                        });
                    }
                }
                else {
                    contactsReference.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(receiverUserID)){

                                currentState = "Friends";
                                sendMessageRequestButton.setText("Remove this Contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // if sender (current User) and receiver both are not same then,
        if (!senderUserID.equals(receiverUserID)) {

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // once a request is send to the user, disable that button...
                    sendMessageRequestButton.setEnabled(false);

                    // if request is not sent to the receiver then,
                    if (currentState.equals("New")) {

                        sendChatRequest();
                    }
                    // if request is already made to the user then,
                    if (currentState.equals("Request Sent")) {

                        cancelChatRequest();
                    }
                    // if request accepted by the receiver then,
                    if (currentState.equals("Request Received")) {

                        acceptChatRequest();
                    }
                    if (currentState.equals("Friends")){

                        removeThatContact();
                    }

                }
            });
        }
        // if both are same then INVISIBLE the send message button for sender i.e. Current Logged In user
        else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void removeThatContact() {

        // using database reference remove the both SENDER ID and RECEIVER ID...
        contactsReference.child(senderUserID).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // using database reference remove the both RECEIVER ID and SENDER ID...
                            contactsReference.child(receiverUserID).child(senderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            // if both ID's are removed successfully
                                            if (task.isSuccessful()) {

                                                // enable send message button, change current state to New and change the button text to SEND MESSAGE
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "New";
                                                sendMessageRequestButton.setText(R.string.visit_send_message);

                                                // make the CANCEL CHAT REQUEST button INVISIBLE and disable it also...
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptChatRequest() {

        contactsReference.child(senderUserID).child(receiverUserID).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            contactsReference.child(receiverUserID).child(senderUserID).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                chatRequestReference.child(senderUserID).child(receiverUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                    chatRequestReference.child(senderUserID).child(receiverUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    currentState = "Friends";
                                                                                    sendMessageRequestButton.setText("Remove this Contact");

                                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineMessageRequestButton.setEnabled(false);

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void cancelChatRequest() {

        // using database reference remove the both SENDER ID and RECEIVER ID...
        chatRequestReference.child(senderUserID).child(receiverUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // using database reference remove the both RECEIVER ID and SENDER ID...
                            chatRequestReference.child(receiverUserID).child(senderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            // if both ID's are removed successfully
                                            if (task.isSuccessful()) {

                                                // enable send message button, change current state to New and change the button text to SEND MESSAGE
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "New";
                                                sendMessageRequestButton.setText(R.string.visit_send_message);

                                                // make the CANCEL CHAT REQUEST button INVISIBLE and disable it also...
                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void sendChatRequest() {

        // using database reference set the Value for REQUEST TYPE to SENT if current state is NEW inside SENDER ID---->RECEIVER ID---->Request Type: Sent
        chatRequestReference.child(senderUserID).child(receiverUserID).child("Request Type").setValue("Sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // using database reference set the Value for REQUEST TYPE to RECEIVED if current state is NEW inside RECEIVER ID---->SENDER ID---->Request Type: Received
                            chatRequestReference.child(receiverUserID).child(senderUserID).child("Request Type").setValue("Received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            // make the send Message Button enabled and change the current state to Request sent
                                            sendMessageRequestButton.setEnabled(true);
                                            currentState = "Request Sent";
                                            // also change the text text for send message Button to CANCEL CHAT REQUEST
                                            sendMessageRequestButton.setText(R.string.cancel_chat_request);

                                        }
                                    });
                        }
                    }
                });

    }
}
