package com.ibm.bluebridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;

import java.net.MalformedURLException;

public class SplashActivity extends AppCompatActivity {
    private static String APPLICATION_ROUTE = "http://school-volunteer-bluebridge.mybluemix.net";
    private static String APPLICATION_GUID = "c5328838-1f5f-4221-8845-7872da171306";

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeNotification();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, EventLoginActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void initializeNotification(){
        // Initialize IBM Bluemix Push Notification SDK
        try {
            // Initialize the SDK for Java (Android) with IBM Bluemix GUID and route
            BMSClient.getInstance().initialize(getApplicationContext(), APPLICATION_ROUTE, APPLICATION_GUID);
        }catch(MalformedURLException e){
            Log.d("BMSClient", "Malformed Bluemix route URL");
        }
        //Initializing client Push SDK
        MFPPush.getInstance().initialize(getApplicationContext());
        MFPPush push = MFPPush.getInstance();

        //For Java (Android)
        MFPPushResponseListener notificationListener = new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("MFPPushResponseListener", "Device registration succeeded.");
            }

            @Override
            public void onFailure(MFPPushException e) {
                Log.d("MFPPushResponseListener", "Device registration failed.");
            }
        };

        push.register(notificationListener);
    }
}
