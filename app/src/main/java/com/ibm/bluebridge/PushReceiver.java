package com.ibm.bluebridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver {
    public PushReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("Push Receiver", "Received a notification");

        String type = intent.getStringExtra("type");

        if(type != null){
            Log.d("Push Receiver", "type gotton:  + type");
        }else{
            Log.d("Push Receiver", "No Type value");
        }

        Intent i = new Intent();
        i.setClassName("com.ibm.bluebridge", "EventLoginActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
