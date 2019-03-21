package com.example.lageraho;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

private View groupFragmentView;
private ListView listView;
private ArrayAdapter<String> arrayAdapter;
private ArrayList<String> list_of_groups = new ArrayList<>();

private DatabaseReference groupRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);

        // created a database reference object for "Groups" section
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        // initialize all the above fields
        initializeFields();

        // method shows the Group Names on the app screen using List View
        retrieveAndDisplayGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get the group name when it is clicked and store it in String type variable
                String currentGroupName = parent.getItemAtPosition(position).toString();

                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", currentGroupName);
                startActivity(groupChatIntent);


            }
        });

        return groupFragmentView;
    }

    private void initializeFields() {

        listView = (ListView) groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        listView.setAdapter(arrayAdapter);
    }

    private void retrieveAndDisplayGroups() {

        // navigate to the FireBase Database using reference object 'groupRef'
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // 'Set' data structure is used to store Unique values, Set doesn't allow to store duplicates values
                Set<String> set = new HashSet<>();

                // uses Iterator to iterator throw each group Name
                Iterator iterator = dataSnapshot.getChildren().iterator();

                // iterate to next group Name if it exists
                while (iterator.hasNext()){

                    // add the group names in Set to remove duplicacy of same group Names
                    set.add(((DataSnapshot)iterator.next()).getKey());

                }

                list_of_groups.clear(); // clear the list view
                list_of_groups.addAll(set);  // add all the group Names
                arrayAdapter.notifyDataSetChanged();  // notify the adapter to reflect the changes on the screen

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
