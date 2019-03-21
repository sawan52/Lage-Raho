package com.example.lageraho;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    // This java file is meant for all the three tabs which consist of chats, groups & contact...

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }


    // return the object of each Tab on selected by user...

    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
            default:
                return null;
        }
    }

    // returns the number of tabs...
    @Override
    public int getCount() {
        return 3;
    }


    // Set the Title of each Tab...

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            default:
                return null;

        }
    }
}
