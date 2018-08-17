package com.phonesender.gavnmandy.mandyphonesender;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    //Components
    EditText usernameEditText;

    //Instances
    User searchedUser;
    String searchedUID;
    List<FriendRequest> requests = new ArrayList<>();
    FriendRequest currentRequest;

    //Firebase
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    ChildEventListener requestListener, acceptListener;


    //Other
    DialogInterface.OnClickListener dialogClickListener, friendDialogListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        usernameEditText = findViewById(R.id.friendUserSearchEditText);

        AddDialogListener();

        AddFriendListeners();

    }

    private void AddFriendListeners() {

        final User currentUser = StaticHolder.currentUser;

        requestListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                currentRequest = dataSnapshot.getValue(FriendRequest.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                builder.setMessage("Would you like to add " + currentRequest.mUser + "?").setPositiveButton("Yes", friendDialogListener)
                        .setNegativeButton("No", friendDialogListener).show();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        acceptListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() != null) {
                    StaticHolder.currentUser.AddFriend(dataSnapshot.getValue(FriendRequest.class));
                    ref.child("accept-notices").child(StaticHolder.currentUser.mUID).child(dataSnapshot.getKey()).setValue(null);
                    StaticHolder.currentUser.UpdateUser();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        ref.child("friend-requests").child(currentUser.mUID).addChildEventListener(requestListener);
        ref.child("accept-notices").child(currentUser.mUID).addChildEventListener(acceptListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User temp = StaticHolder.currentUser;

        ref.child("friend-requests").child(temp.mUID).removeEventListener(requestListener);
        ref.child("accept-notices").child(temp.mUID).removeEventListener(acceptListener);


    }

    public void Search(View v){
        final String nUsername = usernameEditText.getText().toString();

        if(!nUsername.equals("")){
            List<UserInfoPackage> friends = StaticHolder.currentUser.friends;
            for(int i = 0; i < friends.size(); i++){
                if(nUsername.equals(friends.get(i).mUser)) Toast.makeText(this, "You already have this person added.", Toast.LENGTH_SHORT).show();
            }

            ref.child("uids").child(nUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    searchedUID = dataSnapshot.getValue(String.class);
                    if(searchedUID != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                        builder.setMessage("Would you like to add " + nUsername + "?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "This user doesn't exist.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void AddDialogListener(){
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        SendRequest(searchedUID);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        searchedUID = "";
                        break;
                }
            }
        };

        friendDialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case DialogInterface.BUTTON_POSITIVE:
                        User temp = StaticHolder.currentUser;
                        String key = ref.push().getKey();
                        ref.child("accept-notices").child(currentRequest.mUID).child(key).setValue(new FriendRequest(key, temp.mUser, temp.mName, temp.mUID));

                        ref.child("users").child(currentRequest.mUID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                FriendRequest currentReq = dataSnapshot.getValue(FriendRequest.class);
                                StaticHolder.currentUser.AddFriend(currentReq);
                                StaticHolder.targetUser = new UserInfoPackage(currentReq.mUser, currentReq.mName, currentReq.mUID);
                                StaticHolder.currentUser.UpdateUser();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
                ref.child("friend-requests").child(StaticHolder.currentUser.mUID).child(currentRequest.mKey).setValue(null);


            }
        };
    }



    private void SendRequest(String searchedUID){

        String key = ref.child("friend-requests").child(searchedUID).push().getKey();

        User currentUser = StaticHolder.currentUser;

        FriendRequest newReq = new FriendRequest(key, currentUser.mUser, currentUser.mName, currentUser.mUID);

        Map<String, Object> childMap = new HashMap<>();

        childMap.put("/friend-requests/" + searchedUID + "/" + key, newReq);

        ref.updateChildren(childMap);

    }

    public void ToFriends(View v){
        if(StaticHolder.currentUser.friends != null && StaticHolder.currentUser.friends.size() != 0)
        startActivity(new Intent(this, FriendsListActivity.class));
        else{
            Toast.makeText(this, "You do not currently have any friends", Toast.LENGTH_SHORT).show();
        }
    }
}
