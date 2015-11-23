package com.ibm.bluebridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ibm.bluebridge.gcm.QuickstartPreferences;
import com.ibm.bluebridge.gcm.RegistrationIntentService;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;

import java.io.File;

public class SplashActivity extends AppCompatActivity {
    private SessionManager session;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;

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
                Intent i = new Intent(this, EventAdminHomeSpinnerCateActivity.class);
                startActivity(i);
            }
            finish();
            return;
        }

        /*
         * Initialize everything app needs
         */
        try {
            //initializePushNotification();
            initializeGCM();
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

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    private void initializeGCM(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
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

        Log.i("Utils", "Chart directory is " + mPath);
        Utils.setSdDir(sd_dir);
        Utils.setChartDir(mPath);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
