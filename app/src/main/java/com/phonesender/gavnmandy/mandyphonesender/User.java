package com.phonesender.gavnmandy.mandyphonesender;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String mUID, mUser, mName;

    public ArrayList<UserInfoPackage> friends;

    public ArrayList<FriendRequest> requests;

    public User(){

    }

    public User(String mUID, String mName, String mUsername){
        this.mUID = mUID;
        this.mUser = mUsername;
        this.mName = mName;
    }

    public void AddFriend(FriendRequest friend){
        if(friends == null)
            friends = new ArrayList<>();
        friends.add(new UserInfoPackage(friend.mUser, friend.mName, friend.mUID));
    }

    public void UpdateUser(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(mUID).setValue(this);
    }

    public void AddRequest(FriendRequest newReq){
        if(requests == null){
            requests = new ArrayList<>();
        }
        requests.add(newReq);
    }


}
