package com.ibm.bluebridge.util;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;

import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by manirm on 10/26/2015.
 */
public class Utils {

    //Cannot be called before the push notification api is called
    public static String getDeviceId()
    {
        String deviceId = "";
        try {
            deviceId = (String) AuthorizationManager.getInstance().getDeviceIdentity().get("id");
            Log.d("getDeviceId", "MFPPush:computeRegId() - DeviceId obtained from AuthorizationManager object id field is : " + deviceId);
            if(deviceId == null) {
                deviceId = (String)AuthorizationManager.getInstance().getDeviceIdentity().get("deviceId");
                Log.d("getDeviceId", "MFPPush:computeRegId() - DeviceId obtained from AuthorizationManager object deviceId field is : " + deviceId);
            }
        } catch (JSONException var2) {
            var2.printStackTrace();
        }

        return deviceId;
    }

    public static String convertDateFormat(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String resultDate = null;

        try {
            Date date = formatter.parse(dateInString);
            resultDate =  formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultDate;
    }

    public static int[] splitDate(String date) {
        String[] splittedDate = date.split("/");
        int[] splitDate = new int[splittedDate.length];

        splitDate[0] = Integer.parseInt(splittedDate[0]);
        splitDate[1] = Integer.parseInt(splittedDate[1]);
        splitDate[2] = Integer.parseInt(splittedDate[2]);

        //0-year,1-month,2-date
        return splitDate;
    }

    public static int[] splitTime(String time) {
        String[] splittedTime = time.split(":");
        int[] splitTime = new int[splittedTime.length];

        splitTime[0] = Integer.parseInt(splittedTime[0]);
        splitTime[1] = Integer.parseInt(splittedTime[1]);

        //0-year,1-month,2-date
        return splitTime;
    }

    public static void showAlertDialog(String msg, Context ctxt) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctxt);

        // set title
        alertDialogBuilder.setTitle("Message");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK",null);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
