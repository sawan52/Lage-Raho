package com.example.lage_raho.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lage_raho.R;
import com.example.lage_raho.activities.ChatActivity;
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
public class ChatsFragment extends Fragment {

    private View privateChatsView;
    private RecyclerView chatList;

    private DatabaseReference chatReference, usersReference;
    private FirebaseAuth firebaseAuth;

    private String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        chatReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        chatList = privateChatsView.findViewById(R.id.chats_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatReference, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {

                final String usersIDs = getRef(position).getKey();
                final String[] retUserImage = {"default_image"};

                usersReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            if (dataSnapshot.hasChild("Image")) {

                                retUserImage[0] = dataSnapshot.child("Image").getValue().toString();

                                Picasso.get().load(retUserImage[0]).placeholder(R.drawable.profile_image).into(holder.profileImage);
                            }
                            final String retUserName = dataSnapshot.child("Name").getValue().toString();
                            String retUserStatus = dataSnapshot.child("Status").getValue().toString();

                            holder.userName.setText(retUserName);
                            holder.userStatus.setText("Last Seen: " + "\n" + "Date " + " Time");

                            if (dataSnapshot.child("User State").hasChild("state")) {

                                String date = dataSnapshot.child("User State").child("date").getValue().toString();
                                String state = dataSnapshot.child("User State").child("state").getValue().toString();
                                String time = dataSnapshot.child("User State").child("time").getValue().toString();

                                if (state.equals("Online")) {

                                    holder.userStatus.setText("Online");
                                } else if (state.equals("Offline")) {

                                    holder.userStatus.setText("Last Seen: " + date + " " + time);

                                }

                            } else {

                                holder.userStatus.setText("Offline");
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", usersIDs);
                                    chatIntent.putExtra("visit_user_name", retUserName);
                                    chatIntent.putExtra("visit_user_image", retUserImage[0]);
                                    startActivity(chatIntent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                return new ChatsViewHolder(view);
            }
        };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userStatus;
        CircleImageView profileImage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.users_profile_name);
            userStatus = itemView.findViewById(R.id.users_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}
