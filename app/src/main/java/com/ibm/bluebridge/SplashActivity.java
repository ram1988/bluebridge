package com.ibm.bluebridge;

import android.content.Intent;
import android.os.Environment;
import android.sax.StartElementListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import com.ibm.bluebridge.charts.LineActivity;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPush;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushException;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushNotificationListener;
import com.ibm.mobilefirstplatform.clientsdk.android.push.api.MFPPushResponseListener;

import java.io.File;
import java.net.MalformedURLException;

public class SplashActivity extends AppCompatActivity {
    private static String APPLICATION_ROUTE = "http://school-volunteer-bluebridge.mybluemix.net";
    private static String APPLICATION_GUID = "c5328838-1f5f-4221-8845-7872da171306";
    private SessionManager session;


    //private static String APPLICATION_ROUTE = "http://hq-mobile-test.mybluemix.net";
    //private static String APPLICATION_GUID = "4f2f35d9-105e-47af-9394-4036c8af47f5";

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

    MFPPush push = null;
    MFPPushNotificationListener notificationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = SessionManager.getSessionInstance(this);

        if(session.isLoggedIn()){
            Log.i("SplashActivity", "already logged in");
            if(session.isParent()){
                Intent i = new Intent(this, EventParentHomeSpinnerActivity.class);
                startActivity(i);
            }else if(session.isAdmin()){
                Intent i = new Intent(this, EventAdminHomeSpinnerActivity.class);
                startActivity(i);
            }
            finish();
            return;
        }

        /*
         * Initialize everything app needs
         */
        try {
            initializePushNotification();
            initializeSDCardDirectory();
        }catch(Exception e){
            Log.e("SplashActivity", "Excetion while initializing app");
            Log.e("SplashActivity", e.getMessage());
            e.printStackTrace();
        }

        String deviceId = Utils.getDeviceId();
        Log.d("Device ID", deviceId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, EventLoginActivity.class);
                //Intent i = new Intent(SplashActivity.this, LineActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void initializePushNotification(){
        // Initialize IBM Bluemix Push Notification SDK
        try {
            // Initialize the SDK for Java (Android) with IBM Bluemix GUID and route
            BMSClient.getInstance().initialize(getApplicationContext(), APPLICATION_ROUTE, APPLICATION_GUID);
        }catch(MalformedURLException e){
            Log.e("BMSClient", "Malformed Bluemix route URL");
        }
        //Initializing client Push SDK
        MFPPush.getInstance().initialize(getApplicationContext());
        push = MFPPush.getInstance();

        //For Java (Android)
        push.register(new MFPPushResponseListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i("MFPPushResponseListener", "Device registration succeeded.");
            }

            @Override
            public void onFailure(MFPPushException e) {
                Log.e("MFPPushResponseListener", "Device registration failed.");
            }
        });

        if (push != null) {
            push.listen(notificationListener);
        }
    }

    private void initializeSDCardDirectory(){
        String sd_dir = Environment.getExternalStorageDirectory().toString() + "/bluebridge/";
        File homeFolder = new File(sd_dir);
        if(!homeFolder.exists())
            homeFolder.mkdir();

        String mPath = sd_dir + "charts/";
        File chartFolder = new File(mPath);
        if(!chartFolder.exists()){
            chartFolder.mkdir();
            Log.d("SplashActivity", "chart folder has been made. "+mPath);
        }

        Utils.setSdDir(sd_dir);
        Utils.setChartDir(mPath);
    }
}
