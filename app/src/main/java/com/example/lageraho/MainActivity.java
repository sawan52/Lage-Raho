package com.example.lageraho;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;
    private FirebaseUser mFirebaseCurrentUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseCurrentUser = firebaseAuth.getCurrentUser();
        rootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Set the Toolbar for our App
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lage Raho BC...");

        // Set the View Pager for all the three Fragments
        mViewPager = findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        // Set all the three Tabs with customized name for the three Fragments
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if user is not logged In, send him/her to the Login Activity
        if (mFirebaseCurrentUser == null){
            sendUserToLoginActivity();
        }
        else {
            // if he/she is Logged In then,
            // verify whether the user has updated his/her profile or not
            verifyUserExistence();
        }
    }

    private void verifyUserExistence() {
        // get the current user ID
        String currentUserID = mFirebaseCurrentUser.getUid();
        // using FireBase Database reference object to navigate through the database where user info is stored under "Users" section and
        // match for the Logged In user ID and then...
        rootDatabaseReference.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // checks whether his/her Name exists or not...if it exists send him/her a Toast message i.e. "Welcome" otherwise
                if (dataSnapshot.child("Name").exists()){
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                }
                else {
                    // if Name not exists send the user to Settings Activity where they can update their profile first
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_find_friend_option){
            sendUserToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.menu_settings_option){
            sendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.menu_create_group_option){
            requestNewGroup();
        }
        if (item.getItemId() == R.id.menu_signout_option){
            firebaseAuth.signOut();
            sendUserToLoginActivity();
        }
        return true;
    }

    private void sendUserToFindFriendsActivity() {
        Intent findFriendsIntenet = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntenet);

    }

    private void requestNewGroup() {

        // created an Alert Dialog which interacts with user to create a group...
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group name:"); // Display a message that exactly what to do...

        // created an Edit Text field in which user can enter the Group Name
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g: Legends of Tomorrow"); // display the hint of what should be a group name
        builder.setView(groupNameField); // set its view so that it display on Alert Dialog

        // created two buttons
        // positive Button for Creating a group
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // take the groupName from Edit Text and pass it in the method createNewGroup(groupName)
                String groupName = groupNameField.getText().toString();
                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                }
                else {

                    // this method takes groupName as parameter and save that name in FireBase databaseReference as a GroupNames
                    createNewGroup(groupName);
                }
            }
        });

        // negative Button for Canceling a group
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // cancel the Alert Dialog when user clicks on Cancel
                dialog.cancel();
            }
        });

        // show the Alert Dialog box
        builder.show();
    }

    private void createNewGroup(final String groupName) {

        // navigate to the FireBase Database using database reference object under the Groups section and creates a group with
        // the name as it is passed in the method.
        rootDatabaseReference.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // If a group is created successfully, show a Toast message
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
