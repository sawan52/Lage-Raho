package com.example.lage_raho.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lage_raho.R;
import com.example.lage_raho.classes.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    String currentUserID;
    private View contactsView;
    private RecyclerView myContactsList;
    private DatabaseReference contactsReference, userReference;
    private FirebaseAuth firebaseAuth;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contact, container, false);

        myContactsList = contactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsReference, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                String userIDs = getRef(position).getKey();

                userReference.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.child("User State").hasChild("state")) {

                                String date = dataSnapshot.child("User State").child("date").getValue().toString();
                                String state = dataSnapshot.child("User State").child("state").getValue().toString();
                                String time = dataSnapshot.child("User State").child("time").getValue().toString();

                                if (state.equals("Online")) {

                                    holder.onlineIcon.setVisibility(View.VISIBLE);

                                } else if (state.equals("Offline")) {

                                    holder.onlineIcon.setVisibility(View.INVISIBLE);

                                }
                            } else {

                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }

                            if (dataSnapshot.hasChild("Image")) {

                                String userProfileImage = dataSnapshot.child("Image").getValue().toString();
                                String userProfileName = dataSnapshot.child("Name").getValue().toString();
                                String userProfileStatus = dataSnapshot.child("Status").getValue().toString();

                                holder.userName.setText(userProfileName);
                                holder.userStatus.setText(userProfileStatus);

                                Picasso.get().load(userProfileImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            } else {

                                String userProfileName = dataSnapshot.child("Name").getValue().toString();
                                String userProfileStatus = dataSnapshot.child("Status").getValue().toString();

                                holder.userName.setText(userProfileName);
                                holder.userStatus.setText(userProfileStatus);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);

        }
    }
}

