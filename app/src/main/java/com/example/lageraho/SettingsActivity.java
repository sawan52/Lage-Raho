package com.example.lageraho;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettings;
    private EditText setUserName, setUserStatus;
    private CircleImageView userProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        updateAccountSettings = findViewById(R.id.update_setting_button);
        setUserName = findViewById(R.id.set_user_name);
        setUserStatus = findViewById(R.id.set_profile_status);
        userProfileImage = findViewById(R.id.profile_image);
    }
}
