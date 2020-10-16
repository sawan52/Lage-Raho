package com.example.lageraho.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.lageraho.fragments.ChatsFragment;
import com.example.lageraho.fragments.ContactFragment;
import com.example.lageraho.fragments.GroupsFragment;
import com.example.lageraho.fragments.NotesFragment;
import com.example.lageraho.fragments.RequestsFragment;
import com.google.firebase.database.annotations.Nullable;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    // This java file is meant for all the three tabs which consist of chats, groups & contact...
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    // return the object of each Tab on selected by user...
    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
            case 3:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 4:
                NotesFragment notesFragment = new NotesFragment();
                return notesFragment;
            default:
                return null;
        }
    }

    // returns the number of tabs...
    @Override
    public int getCount() {
        return 5;
    }


    // Set the Title of each Tab...
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            /*
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Requests";

            case 4:
                return "Notes";
*/
            default:
                return null;

        }
    }
}
