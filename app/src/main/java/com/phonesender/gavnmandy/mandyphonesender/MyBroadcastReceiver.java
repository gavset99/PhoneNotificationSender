package com.phonesender.gavnmandy.mandyphonesender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
            Intent startServiceIntent = new Intent(context, ListenerService.class);
            context.startService(startServiceIntent);
    }



}
