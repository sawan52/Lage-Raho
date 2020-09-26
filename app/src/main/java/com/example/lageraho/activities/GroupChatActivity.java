package com.example.lageraho.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lageraho.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private EditText messageInputEditText;
    private Toolbar mToolbar;
    private ImageButton sendMessageImageButton;
    private ScrollView mScrollView;
    private TextView mDisplayMessage;

    private String currentGroupName, currentUserID, currentUserName, currentDate, currentTime;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference userDatabaseReference, groupDatabaseReference, groupMessageKeyReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // get the group Name from GroupsFragment using same key "groupName"
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, "Current Group Name: " + currentGroupName, Toast.LENGTH_SHORT).show();


        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = mFirebaseAuth.getCurrentUser().getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        groupDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);


        messageInputEditText = findViewById(R.id.input_group_message);
        sendMessageImageButton = findViewById(R.id.send_message_group_button);
        mScrollView = findViewById(R.id.group_chat_scroll_view);
        mDisplayMessage = findViewById(R.id.group_chat_text_display);


        getUserInfo();


        sendMessageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessageInfoToGroup();

                messageInputEditText.setText("");  // clear the message box after sending the message...

                // it will automatically scroll down to the new message whenever it is going to be send.
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

        groupDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void displayMessages(DataSnapshot dataSnapshot) {

        // iterate for each children in each message key
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){

            // all these four variables must be in same order in which order the data is saved in FireBase Database.
            String chatName = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot) iterator.next()).getValue();

            // append or add the data and display it on the Text View
            mDisplayMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n\n");

            // it will automatically scroll down to the new message whenever it is going to be send.
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }

    }

    private void sendMessageInfoToGroup() {

        // generates a unique key for each user who sends the message
        String messageKey = groupDatabaseReference.push().getKey();

        // get the message enter by user in EditText
        String message = messageInputEditText.getText().toString();

        // checks for the message whether it is empty or not?
        if (TextUtils.isEmpty(message)){

            Toast.makeText(GroupChatActivity.this, "Enter some message first", Toast.LENGTH_SHORT).show();

        }

        else {

            // creates a Calendar instance for Date
            Calendar calendarForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");  // provide the format for Date
            currentDate = currentDateFormat.format(calendarForDate.getTime());

            // creates a Calendar instance for Time
            Calendar calendarForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");  // provide the format for Time
            currentTime = currentTimeFormat.format(calendarForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupDatabaseReference.updateChildren(groupMessageKey);

            // place that message key in corresponding group and provide it a new database reference object as "groupMessageKeyReference"
            groupMessageKeyReference = groupDatabaseReference.child(messageKey);

            // created a HashMap object which will store current message sent userName, inputTextMessage, messageSentDate and Time
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("userName", currentUserName);
            messageInfoMap.put("userSentMessage", message);
            messageInfoMap.put("userSentMessageDate", currentDate);
            messageInfoMap.put("userSentMessageTime",currentTime);

            // and then update it in the corresponding group with unique message key for each user's sent message.
            groupMessageKeyReference.updateChildren(messageInfoMap);
        }

    }

    private void getUserInfo() {

        // uses user database reference object to get the current logged In user ID and then
        userDatabaseReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    // save the userName in currentUserName String type variable
                    currentUserName = dataSnapshot.child("Name").getValue().toString();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

