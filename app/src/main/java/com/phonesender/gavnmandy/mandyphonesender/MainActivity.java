package com.phonesender.gavnmandy.mandyphonesender;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    //Database
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser fUser;


    //Variables
    Boolean isOnline;

    String CHANNEL_ID = "FriendNotif";

    ChildEventListener notificationListener;


    EditText notificationBodyEditText;
    Button sendNotificationButton, toFriendsButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fUser = mAuth.getCurrentUser();

        isOnline = true;

        notificationBodyEditText = findViewById(R.id.notificationBodyEditText);

        sendNotificationButton = findViewById(R.id.notificationSendButton);

        findViewById(R.id.toFriendsButton).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(StaticHolder.currentUser.friends != null && StaticHolder.currentUser.friends.size() != 0)
                    startActivity(new Intent(getApplicationContext(), FriendsListActivity.class));
                else{
                    Toast.makeText(getApplicationContext(), "You do not currently have any friends", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });



        createNotificationChannel();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(StaticHolder.currentUser != null)
        startService(new Intent(this, ListenerService.class));

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(StaticHolder.currentUser != null) {
            StaticHolder.currentUser.UpdateUser();
            AddNotifListener();
        }
        fUser = mAuth.getCurrentUser();

        if(fUser == null && isOnline){
            Toast.makeText(this, "You must login to continue", Toast.LENGTH_SHORT).show();
            Login();
        }
        else if(fUser != null && isOnline)
        {
            AddUserListener();
        }

    }

    private void CreateNewUser() {
        if(StaticHolder.currentUser == null && mAuth.getCurrentUser() != null)
        startActivity(new Intent(this, NewUserActivity.class));
    }

    private void AddUserListener() {

        ref.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if(temp != null && temp != StaticHolder.currentUser) {
                    StaticHolder.currentUser = temp;
                    AddNotifListener();
                    AddTargetUser();
                }
                else
                    CreateNewUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddTargetUser() {
        ref.child("target-users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Integer.class) != null)
                StaticHolder.targetUser = StaticHolder.currentUser.friends.get(dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void SendNotification(View v){
        if(StaticHolder.targetUser == null)
        {
            Toast.makeText(this, "You do not have a targeted user to send this to.", Toast.LENGTH_SHORT).show();
            return;
        }

        String testNotif = notificationBodyEditText.getText().toString();
        if(testNotif.equals("")) {
            Toast.makeText(this, "You must enter a notification body, silly!", Toast.LENGTH_SHORT).show();
            return;
        }
        String notifKey = ref.child("notifications").push().getKey();
        String testTitle = "Notification from " + StaticHolder.currentUser.mUser;

        NotificationInfo newNotif = new NotificationInfo(notifKey, testNotif, testTitle);

        Map<String, Object> childMap = new HashMap<>();

        childMap.put("/notifications/" + StaticHolder.targetUser.mUID + "/" + notifKey, newNotif);

        ref.updateChildren(childMap);

        Toast.makeText(this, "Notification sent to " + StaticHolder.targetUser.mName, Toast.LENGTH_SHORT).show();

        sendNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You must wait at least 10 seconds between notifications.", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendNotificationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SendNotification(view);
                    }
                });
            }
        }, 10000);
    }


    public void Login(){
        final int RC_SIGN_IN = 123;

// ...

// Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }


    public void StartFriends(View v){
        if(StaticHolder.currentUser != null) {
            startActivity(new Intent(this, FriendsActivity.class));
        }
        else
        {
            Toast.makeText(this, "You must be registered to the database", Toast.LENGTH_SHORT).show();
            CreateNewUser();
        }
    }

    public void TestNotification(View v){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("TestNotif")
                .setContentText("TestContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(10, mBuilder.build());
    }


    public void AddNotifListener(){
        User tempUser = StaticHolder.currentUser;

        if(tempUser != null){

            notificationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    NotificationInfo tempNotif = dataSnapshot.getValue(NotificationInfo.class);

                    ref.child("notifications").child(StaticHolder.currentUser.mUID).child(tempNotif.nKey).setValue(null);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                            .setContentTitle(tempNotif.nTitle)
                            .setContentText(tempNotif.nBody)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    notificationManager.notify(10, mBuilder.build());
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

            ref.child("notifications").child(tempUser.mUID).addChildEventListener(notificationListener);
        }
    }

    private void SendNotification(NotificationInfo notification){



    }
}
