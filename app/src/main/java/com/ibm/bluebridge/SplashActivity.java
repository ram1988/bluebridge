package com.ibm.bluebridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import com.ibm.bluebridge.util.Utils;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPSimplePushNotification;

import java.net.MalformedURLException;

public class SplashActivity extends AppCompatActivity {
    private static String APPLICATION_ROUTE = "http://school-volunteer-bluebridge.mybluemix.net";
    private static String APPLICATION_GUID = "c5328838-1f5f-4221-8845-7872da171306";

    //private static String APPLICATION_ROUTE = "http://hq-mobile-test.mybluemix.net";
    //private static String APPLICATION_GUID = "4f2f35d9-105e-47af-9394-4036c8af47f5";

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    MFPPush push = null;
    MFPPushNotificationListener notificationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize IBM Bluemix Push Notification SDK
        try {
            // Initialize the SDK for Java (Android) with IBM Bluemix GUID and route
            BMSClient.getInstance().initialize(getApplicationContext(), APPLICATION_ROUTE, APPLICATION_GUID);
        }catch(MalformedURLException e){
            Log.d("BMSClient", "Malformed Bluemix route URL");
        }
        //Initializing client Push SDK
        MFPPush.getInstance().initialize(getApplicationContext());
        push = MFPPush.getInstance();

        //For Java (Android)
        push.register(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("MFPPushResponseListener", "Device registration succeeded.");

                Intent i = new Intent(SplashActivity.this, EventLoginActivity.class);
                startActivity(i);
            }

            @Override
            public void onFailure(MFPPushException e) {
                Log.d("MFPPushResponseListener", "Device registration failed.");
            }
        });

        String deviceId = Utils.getDeviceId();
        Log.d("Device ID", deviceId);

        //notificationListener = new MFPPushNotificationListener() {
        //    @Override
        //    public void onReceive(final MFPSimplePushNotification message) {
        //        Log.d("Notification Received", message.toString());
        //    }
        //};

        //new Handler().postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
        //        // This method will be executed once the timer is over

        //        // close this activity
        //       finish();
        //    }
        //}, SPLASH_TIME_OUT);
    }

    //@Override
    //protected void onResume() {
    //    super.onResume();
    //    if (push != null) {
    //        push.listen(notificationListener);
    //    }
    //}

}
