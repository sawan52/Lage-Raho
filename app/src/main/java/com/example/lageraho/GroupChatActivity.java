package com.example.lageraho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupChatActivity extends AppCompatActivity {

    private EditText messageInputEditText;
    private Toolbar mToolbar;
    private ImageButton sendMessageImageButton;
    private ScrollView mScrollView;
    private TextView mDisplayMessage;

    private String currentGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, "Current Group Name: " + currentGroupName, Toast.LENGTH_SHORT).show();

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);


        messageInputEditText = findViewById(R.id.input_group_message);
        sendMessageImageButton = findViewById(R.id.send_message_group_button);
        mScrollView = findViewById(R.id.group_chat_scroll_view);
        mDisplayMessage = findViewById(R.id.group_chat_text_display);
    }
}
