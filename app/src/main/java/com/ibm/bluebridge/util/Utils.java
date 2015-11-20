package com.ibm.bluebridge.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;

import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by manirm on 10/26/2015.
 */
public class Utils {
    private static String SD_DIR;

    private static String CHART_DIR;

    public static String getSdDir() {
        return SD_DIR;
    }

    //Should be set in Splash Page
    public static void setSdDir(String sdDir) {
        SD_DIR = sdDir;
    }

    public static String getChartDir() {
        return CHART_DIR;
    }

    //Should be set in Splash Page
    public static void setChartDir(String chartDir) {
        CHART_DIR = chartDir;
    }

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

    public static long getDateInMillis(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);

        return calendar.getTimeInMillis();
    }

    public static void sendEmail(String email, Chart chart, Context context){
        Date now = new Date();
        String date = android.text.format.DateFormat.format("yyyy-MM-dd-hh-mm-ss", now).toString();

        // image naming and path to include sd card appending name you choose for file
        String snapshotPath = Utils.getChartDir();
        String snapshotFilename = date + ".jpg";

        chart.saveToPath(snapshotPath , snapshotFilename);

        File imageFile = new File(snapshotPath + snapshotFilename);
        Uri uri = Uri.fromFile(imageFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        String uriText = "mailto:" + Uri.encode(email) +
                "?subject=" + Uri.encode("BlueBridge - chart email") +
                "&body=" + Uri.encode("the body of the message");
        Uri emailuri = Uri.parse(uriText);
        intent.setData(emailuri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(intent);
    }




    public static class LoaderDialog extends AsyncTask<Void, Void, Void> {

        private ProgressDialog ringProgressDialog;
        private String message;
        private boolean stop;

        /*public LoaderDialog(Context ctxt, String message) {
            super();
            System.out.println("Laoder dialog--->" + message);
            ringProgressDialog = new ProgressDialog(ctxt);
            this.message = message;
        }*/

        public void closeDialog() {
            stop = true;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            System.out.println("Prexecute dialog111111--->" + message);
            ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            ringProgressDialog.setMessage("Loading...");
            ringProgressDialog.setIndeterminate(true);
            ringProgressDialog.setCancelable(true);
            ringProgressDialog.show();
        }


        protected void onPostExecute(Void value) {

            super.onPostExecute(value);
            ringProgressDialog.dismiss();
        }
    }


  /*  public static class LoaderDialog  {

        private ProgressDialog ringProgressDialog;
        private String message;
        private boolean stop;

        public LoaderDialog(final Context ctxt, String msg) {
            System.out.println("Laoder dialog--->" + msg);
            this.message = msg;

            ringProgressDialog = new ProgressDialog(ctxt);
            ringProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            ringProgressDialog.setMessage(message);
            ringProgressDialog.setIndeterminate(true);
            ringProgressDialog.setCancelable(true);
            ringProgressDialog.show();

            Thread mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    System.out.println("Start running");

                    while(!stop) {

                    }

                    ringProgressDialog.dismiss();
                    System.out.println("Stop running");



                }
            });

            mThread.start();
        }

        public void closeDialog() {
            ringProgressDialog.dismiss();
            stop = true;
        }
    }
 */
}
