package com.phonesender.gavnmandy.mandyphonesender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.UserInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {

    private ArrayList<UserInfoPackage> mFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        InitFriends();

    }

    private void InitFriends(){
        mFriends = StaticHolder.currentUser.friends;
        InitAdapter();
    }

    private void InitAdapter(){

        RecyclerView recyclerView = findViewById(R.id.friendListRecycler);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mFriends);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
