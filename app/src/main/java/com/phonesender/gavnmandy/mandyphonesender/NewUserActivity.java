package com.phonesender.gavnmandy.mandyphonesender;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class NewUserActivity extends AppCompatActivity {

    Button applyButton;
    EditText usernameEditText, nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);


        usernameEditText = findViewById(R.id.usernameEditText);
        nameEditText = findViewById(R.id.nameEditText);

        Toast.makeText(this, "You must register yourself to use this app.", Toast.LENGTH_SHORT).show();


    }


    public void CreateUser(View v){
        String nUsername, nName;


        nUsername = usernameEditText.getText().toString();
        nName = nameEditText.getText().toString();


        if(!nUsername.equals("") && !nName.equals("")){
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

            User tempUser = new User(fUser.getUid(), nName, nUsername);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();



            ref.child("users").child(tempUser.mUID).setValue(tempUser);

            ref.child("uids").child(tempUser.mUser).setValue(tempUser.mUID);

            StaticHolder.currentUser = tempUser;

            finish();
        }
        else
        {
            Toast.makeText(this, "Both fields must be filled out.", Toast.LENGTH_SHORT).show();
        }

    }

}
