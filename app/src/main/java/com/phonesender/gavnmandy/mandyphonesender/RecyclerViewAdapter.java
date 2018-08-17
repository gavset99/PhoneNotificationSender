package com.phonesender.gavnmandy.mandyphonesender;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mUsernames;

    private ArrayList<UserInfoPackage> mFriends;

    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<UserInfoPackage> mFriends) {
        this.mFriends = mFriends;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_friend, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {
        UserInfoPackage tempInfo = mFriends.get(position);

        String display = tempInfo.mName + "\n(" + tempInfo.mUser + ")";

        holder.fUsername.setText(display);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticHolder.targetUser = mFriends.get(holder.getAdapterPosition());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("target-users").child(StaticHolder.currentUser.mUID).setValue(holder.getAdapterPosition());
                Toast.makeText(mContext, StaticHolder.targetUser.mName + " is now your target user.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        UserInfoPackage fInfo;
        TextView fUsername;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fUsername = itemView.findViewById(R.id.friendUsername);
            parentLayout = itemView.findViewById(R.id.friendParentLayout);
        }
    }
}
