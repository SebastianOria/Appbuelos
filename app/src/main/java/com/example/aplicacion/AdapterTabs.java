package com.example.aplicacion;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class AdapterTabs extends FragmentPagerAdapter {

    public AdapterTabs(@NonNull FragmentManager fm) {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position){

        switch (position){
            case 0:
                ChatsFragments chatsFragments = new ChatsFragments();
                return chatsFragments;
            default:
                return null;

        }

    }
    @Override
    public int getCount() {
        return 1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Chats";
            default:
                return null;

        }


    }


}
