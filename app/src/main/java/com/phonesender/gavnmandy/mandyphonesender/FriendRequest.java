package com.phonesender.gavnmandy.mandyphonesender;

public class    FriendRequest {

    public String mKey, mUser, mName, mUID;

    public FriendRequest(){

    }

    public FriendRequest(String key, String user, String name, String UID){
        mKey = key;
        mUser = user;
        mName = name;
        mUID = UID;
    }
}